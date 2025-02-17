package com.mildo.dev.api.problem.service;

import com.mildo.dev.api.problem.domain.dto.response.ProblemListResponse;
import com.mildo.dev.api.problem.domain.dto.request.ProblemSolverDto;
import com.mildo.dev.api.problem.repository.ProblemRepository;
import com.mildo.dev.api.problem.repository.dto.ProblemListDslDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemListResponse getProblemList(String memberId, String studyId, String category, String title, int  page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 1. 문제 가져오기
        List<ProblemListDslDto> results = problemRepository.findFilteredProblemList(title, category, memberId, pageable);

        // 2. 문제 번호만 뽑아 오기
        List<Long> problemNos = results.stream()
                .map(ProblemListDslDto::getProblemId)
                .collect(Collectors.toList());

        // 3. 문제 번호들로 우리 스터디원이 풀었으면 프로필 이미지 가져오기
        List<ProblemSolverDto> solvers = problemRepository.findSolversByProblemNos(problemNos, studyId);

        // 4. 문제들과 풀었는 문제들 맞춰 넣어 주기
        Map<Long, List<String>> problemSolverMap = solvers.stream()
                .collect(Collectors.groupingBy(ProblemSolverDto::getProblemNo,
                        Collectors.mapping(ProblemSolverDto::getImgUrl, Collectors.toList())));

        return ProblemListResponse.problemDto(results, problemSolverMap);
    }

}