package com.podofarm.dev.api.problem.controller;

import com.podofarm.dev.api.member.customoauth.dto.CustomUser;
import com.podofarm.dev.api.problem.domain.dto.response.ProblemListResponse;
import com.podofarm.dev.api.problem.domain.dto.response.ProblemStaticDto;
import com.podofarm.dev.api.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/problem")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemService problemService;


    //이 API는 스터디 구성원이 다 사용하기 때문에 요청때마다 조회하는것이 아니라 캐시 데이터를 이용하여
    //DB 조회를 최소화
    @GetMapping("/{studyId}/problem-list")
    public ResponseEntity<ProblemListResponse> getProblemList(@AuthenticationPrincipal CustomUser customUser,
                                                                  @PathVariable String studyId,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) String title,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
      ProblemListResponse response = problemService.getProblemList(customUser.getMemberId(), studyId, category, title, page, size);
      return ResponseEntity.ok(response);
    }

    @GetMapping("/{problemId}/static-info")
    public ResponseEntity<?> staticInfo(@PathVariable String problemId) {
        ProblemStaticDto problemDto = problemService.getFormattedProblemInfo(Long.parseLong(problemId));

        Map<String, String> response = new HashMap<>();
        response.put("readme", problemDto.getReadme());
        response.put("title", problemDto.formatTitle());
        return ResponseEntity.ok(response);
    }



}
