package com.mildo.dev.api.problem.controller;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.problem.domain.dto.response.ProblemListImgDto;
import com.mildo.dev.api.problem.domain.dto.response.ProblemListResponse;
import com.mildo.dev.api.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problem")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/{studyId}/problem-list")
    public ResponseEntity<ProblemListResponse> getProblemList(@AuthenticationPrincipal CustomUser customUser,
                                                                  @PathVariable String studyId,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) String title,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
      ProblemListResponse response = problemService.getProblemList(customUser.getMemberId(), studyId, category, title, page, size);
      return ResponseEntity.ok(response);
    }


}
