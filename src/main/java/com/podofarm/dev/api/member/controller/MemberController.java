package com.podofarm.dev.api.member.controller;

import com.amazonaws.AmazonServiceException;
import com.podofarm.dev.api.code.domain.dto.CodeInfoDTO;
import com.podofarm.dev.api.code.service.CodeService;
import com.podofarm.dev.api.member.customoauth.dto.CustomUser;
import com.podofarm.dev.api.member.domain.dto.request.MemberReNameDto;
import com.podofarm.dev.api.member.domain.dto.request.TokenDto;
import com.podofarm.dev.api.member.domain.dto.response.*;
import com.podofarm.dev.api.member.service.MemberService;
import com.podofarm.dev.api.utils.cookie.CookieUtil;
import com.podofarm.dev.global.exception.exceptionClass.ServerUnstableException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService userService;
    private final CodeService codeService;

    @ResponseBody
    @PostMapping(value = "/tokens", produces = "application/json; charset=UTF-8")
    public TokenResponse generateToken(@RequestBody TokenDto memberId, HttpServletResponse response){
        TokenDto res = userService.generateToken(memberId.getMemberId());
        Cookie refreshTokenCookie = CookieUtil.createCookie("RefreshToken", res.getRefreshToken(), -1);
        response.addCookie(refreshTokenCookie);
        return new TokenResponse(res.getMemberId(), res.getAccessToken());
    }

    @ResponseBody
    @PostMapping(value="/tokens/refresh", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> getCookieValue(@CookieValue(name = "RefreshToken", required = false) String RefreshToken, HttpServletRequest request) {
        if (RefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookie is missing");
        }

        TokenResponse res = userService.refreshNew(RefreshToken);
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping(value="/{memberId}/solved/problem", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> solvedProblem(@PathVariable String memberId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String title)
    {
        SolvedListResponse solvedProblemList = userService.solvedProblemList(memberId, page, size, title);
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
    @GetMapping(value = "/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> memberInfo(@AuthenticationPrincipal CustomUser customUser)
    {
        MemberInfoResponse memberInfo = userService.memberInfo(customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }

    @ResponseBody
    @PatchMapping(value = "/info", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> updateUser(@Valid @RequestBody MemberReNameDto nameDto,
                                        @AuthenticationPrincipal CustomUser customUser)
    {
        MemberInfoResponse res = userService.updateUser(nameDto, customUser.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ResponseBody
    @PatchMapping(value="/{memberId}/upload", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @PathVariable String memberId,
                                        @AuthenticationPrincipal CustomUser customUser)
    {
        try {
            MemberInfoResponse dto = userService.uploadImg(file, memberId, customUser.getMemberId());
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
        userService.deleteMember(memberId, customUser.getMemberId());
        return ResponseEntity.ok("삭제 성공");
    }

    @ResponseBody
    @GetMapping(value="/problem/{memberId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> problem(@PathVariable String memberId,
                                     @AuthenticationPrincipal CustomUser customUser)
    {
        ProblemPageInfoResponse res = userService.problemPageInfo(memberId, customUser.getMemberId());
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping(value="/{studyId}/solved/{memberId}", produces="application/json; charset=UTF-8")
    public ResponseEntity<?> solvedMember(@PathVariable String memberId,
                                          @PathVariable String studyId,
                                          @AuthenticationPrincipal CustomUser customUser)
    {
        Optional<SolvedMemberListResponse> res = userService.solvedMember(memberId, studyId);
        return ResponseEntity.ok(res);
    }

    /*
    * MEMBER별 푼 문제 API
    * */

    @GetMapping("/{memberId}/problem/{problemId}/solved-info")
    public ResponseEntity<List<CodeInfoDTO>> memberSolvedInfo(@PathVariable String problemId,
                                                              @PathVariable String memberId) {
        List<CodeInfoDTO> codeList = codeService.getMemberSolvedInfo(memberId, Long.parseLong(problemId));
        return ResponseEntity.ok(codeList);
    }

    @PatchMapping("/edit-code")
    public ResponseEntity<?> memberSolvedEdit(@RequestBody Map<String, String> requestData) {
        String memberId = requestData.get("memberId");
        String problemId = requestData.get("problemId");
        String code = requestData.get("code");

        String response = codeService.memberSolvedEdit(memberId, problemId, code);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-code")
    public ResponseEntity<?> memberSolvedDelete(@RequestBody Map<String, String> requestData) {
        String memberId = requestData.get("memberId");
        String problemId = requestData.get("problemId");

        String response = codeService.memberSolvedDelete(memberId, problemId);
        return ResponseEntity.ok(response);
    }
}
