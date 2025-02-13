package com.mildo.dev.api.member.controller;

import com.amazonaws.AmazonServiceException;
import com.mildo.dev.api.code.domain.dto.response.SolvedListResponse;
import com.mildo.dev.api.code.domain.dto.response.SolvedProblemResponse;
import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.member.domain.dto.request.MemberReNameDto;
import com.mildo.dev.api.member.domain.dto.request.TokenDto;
import com.mildo.dev.api.member.domain.dto.response.MemberInfoDTO;
import com.mildo.dev.api.member.domain.dto.response.ProblemMemberDto;
import com.mildo.dev.api.member.domain.dto.response.SolvedMemberListDto;
import com.mildo.dev.api.member.domain.dto.response.TokenResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService userService;

    @ResponseBody
    @PostMapping(value = "/tokens", produces = "application/json; charset=UTF-8")
    public TokenResponse tokenMake(@RequestBody TokenResponse memberId, HttpServletResponse response){
        try{
            TokenDto res = userService.token(memberId.getMemberId());
            Cookie refreshTokenCookie = CookieUtil.createCookie("RefreshToken", res.getRefreshToken(), -1);
            response.addCookie(refreshTokenCookie);
            return new TokenResponse(res.getMemberId(), res.getAccessToken());
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
            TokenResponse res = userService.refreshNew(RefreshToken);
            return ResponseEntity.ok(res);
        }
        catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping(value="/{memberId}/level", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> levelCount(@PathVariable String memberId,
                                        @AuthenticationPrincipal CustomUser customUser)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }

        SolvedProblemResponse LevelCount = userService.memberLevel(customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(LevelCount);
    }

    @ResponseBody
    @GetMapping(value="/{memberId}/solved/problem", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> solvedProblem(@PathVariable String memberId,
                                           @AuthenticationPrincipal CustomUser customUser,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String title)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }

        SolvedListResponse solvedProblemList = userService.solvedProblemList(customUser.getMemberId(), page, size, title);
        return ResponseEntity.status(HttpStatus.OK).body(solvedProblemList);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUser customUser,
                                    HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication)
    {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        userService.tokenDelete(customUser.getMemberId());
        CookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }

    @ResponseBody
    @GetMapping(value = "/member/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> memberInfo(@AuthenticationPrincipal CustomUser customUser)
    {
        MemberInfoDTO memberInfo = userService.memberInfo(customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }

    @ResponseBody
    @PatchMapping(value = "/member/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> updateUser(@Valid @RequestBody MemberReNameDto nameDto,
                                        @AuthenticationPrincipal CustomUser customUser)
    {
        MemberInfoDTO res = userService.updateUser(nameDto, customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ResponseBody
    @PatchMapping(value="/{memberId}/upload", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @AuthenticationPrincipal CustomUser customUser,
                                        @PathVariable String memberId)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }

        try {
            MemberInfoDTO dto = userService.uploadImg(file, customUser.getMemberId());
            return ResponseEntity.ok(dto);
        } catch (IOException e) {
            throw  new RuntimeException("File not found");
        }catch (AmazonServiceException e) {
            throw new ServerUnstableException("S3 업로드 실패");
        }
    }

    @ResponseBody
    @DeleteMapping(value="/{memberId}/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> deleteMember(@PathVariable String memberId,
                                          @AuthenticationPrincipal CustomUser customUser)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }

        userService.deleteMember(customUser.getMemberId());
        return ResponseEntity.ok("삭제 성공");
    }

    @ResponseBody
    @GetMapping(value="/problem/{memberId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> problem(@PathVariable String memberId,
                                     @AuthenticationPrincipal CustomUser customUser)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }
        ProblemMemberDto res = userService.problemMember(customUser.getMemberId());
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping(value="/{studyId}/solved/{memberId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> solvedMember(@PathVariable String memberId,
                                          @PathVariable String studyId,
                                          @AuthenticationPrincipal CustomUser customUser)
    {
        if (!memberId.equals(customUser.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("memberId와 로그인한 사용자의 ID가 다릅니다.");
        }

        Optional<SolvedMemberListDto> res = userService.solvedMember(customUser.getMemberId(), studyId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/test")
    public String Test(){
        return "TEST";
    }
}
