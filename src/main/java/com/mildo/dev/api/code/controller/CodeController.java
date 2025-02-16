package com.mildo.dev.api.code.controller;

import com.mildo.dev.api.code.domain.dto.request.CommentContentDTO;
import com.mildo.dev.api.code.domain.dto.response.CommentResponse;
import com.mildo.dev.api.code.service.CodeService;
import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.member.domain.dto.request.MemberReNameDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @ResponseBody
    @GetMapping(value = "/code/{codeNo}/comment", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> commentList(@PathVariable Long codeNo)
    {
        List<CommentResponse> list = codeService.allComment(codeNo);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @ResponseBody
    @PostMapping(value = "/code/{codeNo}/comment", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> commentPost(@PathVariable Long codeNo,
                                         @AuthenticationPrincipal CustomUser customUser,
                                         @Valid @RequestBody CommentContentDTO comment)
    {
        CommentResponse res = codeService.insertComment(codeNo, comment.getCommentContent(), customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
