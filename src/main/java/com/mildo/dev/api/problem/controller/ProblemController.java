package com.mildo.dev.api.problem.controller;

import com.mildo.dev.api.problem.domain.dto.ProblemListDto;
import com.mildo.dev.api.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/problem")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/problem-list")
    public ResponseEntity<List<ProblemListDto>> getProblemList() {
        List<ProblemListDto> response = problemService.getProblemList();
        return ResponseEntity.ok(response);
    }
}
