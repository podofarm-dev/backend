package com.mildo.dev.api.member.controller;

import com.mildo.dev.api.member.domain.dto.TokenDto;
import com.mildo.dev.api.member.domain.dto.TokenRedis;
import com.mildo.dev.api.member.service.MemberService;
import com.mildo.dev.api.utils.cookie.CookieUtil;
import com.mildo.dev.global.exception.exceptionClass.TokenException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService userService;

    @ResponseBody
    @PostMapping(value = "/tokens", produces = "application/json; charset=UTF-8")
    public TokenRedis tokenMake(@RequestBody TokenRedis memberId, HttpServletResponse response){
        try{
            TokenDto res = userService.token(memberId.getMemberId());
            Cookie refreshTokenCookie = CookieUtil.createCookie("RefreshToken", res.getRefreshToken(), -1);
            response.addCookie(refreshTokenCookie);
            return new TokenRedis(res.getMemberId(), res.getAccessToken());
        }catch (RuntimeException ex){
            throw  new RuntimeException("Member not found");
        }
    }

    @ResponseBody
    @PostMapping(value="/tokens/refresh", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> getCookieValue(@CookieValue(name = "RefreshToken", required = false) String RefreshToken, HttpServletRequest request) {
        if (RefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookie is missing");
        }
        try {
            String res = userService.refreshNew(RefreshToken);
            return ResponseEntity.ok(res);
        }
        catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public String Test(){
        return "TEST";
    }
}
