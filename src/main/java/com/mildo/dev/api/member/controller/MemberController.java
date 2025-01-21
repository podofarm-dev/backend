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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService userService;

    @GetMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal CustomUser customUser){
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try{
            TokenRedis res = userService.token(customUser);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (RedisConnectionFailureException e){
            return ResponseEntity.status(HttpStatus.OK).body("RedisConnectionFailureException !!!");
        }
    }

    @GetMapping("/loginFailure")
    public ResponseEntity<?> loginFailure(@RequestParam(required = false) String error){
        log.info("error = {}", error);
        return ResponseEntity.status(HttpStatus.OK).body("로그인 실패");
    }

    @GetMapping("/Test")
    public String loginFailure(){
        String userId = CodeGenerator.generateUserId();
        log.info("userId = {}", userId);
        return "TEST";
    }

}
