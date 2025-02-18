package com.mildo.dev.api.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mildo.dev.api.code.service.CodeService;
import com.mildo.dev.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mildo.dev.api.code.domain.dto.request.CommentContentDTO;
import com.mildo.dev.api.code.domain.dto.response.CommentResponse;
import com.mildo.dev.api.code.domain.dto.response.CommentListResponse;
import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.text.ParseException;


@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
@Slf4j
public class CodeController {

    private final MemberService memberService;
    private final CodeService codeService;

    @CrossOrigin(origins = "chrome-extension://magnaalaamndcofdpgeicpnlpdjajbjb")
    @PostMapping("/receive-sync")
    public ResponseEntity<String> receiveSync(@RequestBody String data) {
        try {
            ObjectMapper sync = new ObjectMapper();
            JsonNode convertSync = sync.readTree(data);
            System.out.println(data + "data test");
            if (validateUserStudySync(convertSync)) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.ok("fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request");
        }
    }

    @CrossOrigin(origins = {"chrome-extension://kmleenknngfkjncchnbfenfamoighddf", "https://school.programmers.co.kr"})
    @PostMapping("/receive-data")
    public ResponseEntity<String> receiveData(@RequestBody String data) {
        try {

            ObjectMapper Data = new ObjectMapper();
            JsonNode convertData = Data.readTree(data);

            log.info(data);
            if (validateUserStudySync(convertData)) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.ok("fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request");
        }
    }

    @CrossOrigin(origins = {"chrome-extension://kmleenknngfkjncchnbfenfamoighddf", "https://school.programmers.co.kr"})
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseEntity<String> upload(@RequestBody String request) throws JsonProcessingException, ParseException {
        ObjectMapper Data = new ObjectMapper();
        JsonNode convertData = Data.readTree(request);

        codeService.upload(convertData);
        return ResponseEntity.ok("Upload successful");
    }


    private boolean validateUserStudySync(JsonNode convertData) {
        // 필요한 정보 추출
        String userId = convertData.get("id").asText();
        String studyId = convertData.get("studyId").asText();

        return memberService.checkExtensionSync(userId, studyId);
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
}




