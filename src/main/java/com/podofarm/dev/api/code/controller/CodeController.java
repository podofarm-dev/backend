package com.podofarm.dev.api.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podofarm.dev.api.code.domain.dto.UploadDTO;
import com.podofarm.dev.api.code.domain.dto.request.CacheRequestDTO;
import com.podofarm.dev.api.code.service.CodeService;
import com.podofarm.dev.api.member.service.MemberService;
import com.podofarm.dev.api.code.domain.dto.request.OpenAIRequest;
import com.podofarm.dev.api.code.domain.dto.response.OpenAIResponse;
import com.podofarm.dev.api.code.service.CodeService;
import com.podofarm.dev.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import com.podofarm.dev.api.code.domain.dto.request.CommentContentDTO;
import com.podofarm.dev.api.code.domain.dto.response.CommentResponse;
import com.podofarm.dev.api.code.domain.dto.response.CommentListResponse;
import com.podofarm.dev.api.member.customoauth.dto.CustomUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
@Slf4j
public class CodeController {

    private final MemberService memberService;
    private final CodeService codeService;

    @PostMapping("/receive-sync")
    public ResponseEntity<String> receiveSync(@RequestBody Map<String, String> requestBody) {
        String memberId = requestBody.get("id");
        String studyId = requestBody.get("studyId");

        if (memberService.checkExtensionSync(memberId, studyId)) {
            codeService.fetchData(memberId);
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.ok("fail");
        }
    }

    @GetMapping("/fetchDataFromServer")
    public ResponseEntity<Map<String, Object>> fetchDataFromServer(@RequestParam String id) {
        List<Long> problemIdList = codeService.getProblemIdList(id);
        if (problemIdList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "아직 데이터가 준비되지 않았습니다."));
        }
        return ResponseEntity.ok(Collections.singletonMap("problemIdList", problemIdList));
    }


    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody String request) throws JsonProcessingException, ParseException {
        ObjectMapper Data = new ObjectMapper();
        JsonNode convertData = Data.readTree(request);

        UploadDTO upload = new UploadDTO(convertData);
        String source = upload.getSource();
        String memberId = upload.getMemberId();
        String problemId = upload.getProblemId();

        //@async
        codeService.upload(upload);
        codeService.openAI(source, memberId, problemId);

        return ResponseEntity.ok("Podofarm 업로드 완료");
    }

    //캐시관련

    @PostMapping("/cacheData")
    public Map<String, String> receiveCacheData(@RequestBody CacheRequestDTO requestData) {
        String id = requestData.getMemberId();
        Long problemId = requestData.getProblemId();

        // 캐시에 저장
        return codeService.cacheSyncData(id, problemId);
    }

    // 캐시에서 데이터 조회 (테스트용)
    @GetMapping("/cacheData/{id}")
    public Map<String, String> getCachedData(@PathVariable String id) {
        return codeService.getCachedData(id);
    }




    @ResponseBody
    @GetMapping(value = "/{codeNo}/comment", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> commentList(@PathVariable Long codeNo)
    {
        CommentListResponse list = codeService.allComment(codeNo);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @ResponseBody
    @PostMapping(value = "/{codeNo}/comment", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> commentPost(@PathVariable Long codeNo,
                                         @AuthenticationPrincipal CustomUser customUser,
                                         @Valid @RequestBody CommentContentDTO comment)
    {
        CommentResponse res = codeService.insertComment(codeNo, comment.getCommentContent(), customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ResponseBody
    @DeleteMapping(value = "/{codeNo}/comment/{commentNo}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> commentDelete(@PathVariable Long codeNo,
                                           @PathVariable Long commentNo,
                                           @AuthenticationPrincipal CustomUser customUser)
    {
        codeService.deleteComment(codeNo, commentNo, customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 성공!");
    }

    @ResponseBody
    @PatchMapping(value = "/{codeNo}/comment/{commentNo}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> updateUser(@PathVariable Long codeNo,
                           @PathVariable Long commentNo,
                           @Valid @RequestBody CommentContentDTO comment,
                           @AuthenticationPrincipal CustomUser customUser)
    {
        CommentResponse resComment = codeService.updateComment(codeNo, commentNo, comment.getCommentContent(), customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(resComment);
    }

    @PostMapping("/analyze")
    public OpenAIResponse analyzeCode(@RequestBody String request) {
        return codeService.analyzeCode(request);
    }
}



