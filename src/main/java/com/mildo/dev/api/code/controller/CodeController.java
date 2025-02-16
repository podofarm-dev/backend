package com.mildo.dev.api.code.controller;

import com.mildo.dev.api.code.domain.dto.response.CommentResponse;
import com.mildo.dev.api.code.service.CodeService;
import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @ResponseBody
    @GetMapping(value = "/code/{codeNo}/comment", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> memberInfo(@PathVariable Long codeNo)
    {
        List<CommentResponse> list = codeService.allComment(codeNo);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
