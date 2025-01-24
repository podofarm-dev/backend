package com.mildo.dev.api.member.controller;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.member.domain.dto.TokenRedis;
import com.mildo.dev.api.member.service.MemberService;
import com.mildo.dev.api.utils.Random.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService userService;

    @ResponseBody
    @PostMapping(value = "/tokens", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> tokenMake(@RequestBody TokenRedis memberId){

        TokenRedis res = userService.token(memberId.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/test")
    public String Test(){
        return "TEST";
    }
}
