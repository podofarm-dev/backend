package com.mildo.dev.api.member.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.mildo.dev.api.code.domain.dto.CodeLevelDTO;
import com.mildo.dev.api.code.domain.dto.CodeSolvedListDTO;
import com.mildo.dev.api.code.domain.dto.SolvedListResponse;
import com.mildo.dev.api.code.domain.dto.SolvedProblemResponse;
import com.mildo.dev.api.member.domain.dto.MemberInfoDTO;
import com.mildo.dev.api.member.domain.dto.ProblemMemberDto;
import com.mildo.dev.api.member.domain.dto.TokenDto;
import com.mildo.dev.api.member.domain.dto.TokenRedis;
import com.mildo.dev.api.member.service.MemberService;
import com.mildo.dev.api.utils.cookie.CookieUtil;
import com.mildo.dev.global.exception.exceptionClass.ServerUnstableException;
import com.mildo.dev.global.exception.exceptionClass.TokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
            TokenRedis res = userService.refreshNew(RefreshToken);
            return ResponseEntity.ok(res);
        }
        catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping(value="/{memberId}/level", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> levelCount(@PathVariable String memberId) {
        SolvedProblemResponse LevelCount = userService.memberLevel(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(LevelCount);
    }

    @ResponseBody
    @GetMapping(value="/{memberId}/solved/problem", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> solvedProblem(@PathVariable String memberId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String title)
    {
        SolvedListResponse solvedProblemList = userService.solvedProblemList(memberId, page, size, title);
        return ResponseEntity.status(HttpStatus.OK).body(solvedProblemList);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenRedis memberId, HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        userService.tokenDelete(memberId.getMemberId());
        CookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }

    @ResponseBody
    @GetMapping(value = "/member/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> memberInfo(@RequestBody TokenRedis vo) {
        MemberInfoDTO memberInfo = userService.memberInfo(vo.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }

    @ResponseBody
    @PatchMapping(value = "/member/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> updateUser(@Valid @RequestBody MemberInfoDTO vo) {
        MemberInfoDTO res = userService.updateUser(vo);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ResponseBody
    @PatchMapping(value="/{memberId}/upload", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String memberId) {
        try {
            MemberInfoDTO dto = userService.uploadImg(file, memberId);
            return ResponseEntity.ok(dto);
        } catch (IOException e) {
            throw  new RuntimeException("File not found");
        }catch (AmazonServiceException e) {
            throw new ServerUnstableException("S3 업로드 실패");
        }
    }

    @ResponseBody
    @DeleteMapping(value="/member/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> deleteMember(@RequestBody MemberInfoDTO vo) {
        userService.deleteMember(vo);
        return ResponseEntity.ok("삭제 성공");
    }

    @ResponseBody
    @GetMapping(value="/problem/member", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> problem(@RequestBody MemberInfoDTO vo) {
        ProblemMemberDto res = userService.problemMember(vo);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/test")
    public String Test(){
        return "TEST";
    }
}
