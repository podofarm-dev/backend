package com.mildo.dev.api.problem.service;

import com.mildo.dev.api.problem.domain.dto.response.ProblemListResponse;
import com.mildo.dev.api.problem.domain.dto.request.ProblemSolverDto;
import com.mildo.dev.api.problem.domain.dto.response.ProblemTitleDto;
import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import com.mildo.dev.api.problem.repository.ProblemRepository;
import com.mildo.dev.api.problem.repository.dto.ProblemListDslDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

//    public List<ProblemListImgDto> getProblemList(String memberId, String studyId, String category, String title, int  page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        List<ProblemListDto> results;
//        if (title != null && !title.isEmpty()) { // 1. 검색 조건이 있음
//            if ("Y".equals(category)) {
//                results = problemRepository.findAllTitleCategoryProblemListWithCategoryY(title, memberId, category, pageable); // 1-1 검색 O 풀었는 문제 순 O
//            } else if ("N".equals(category)) {
//                results = problemRepository.findAllTitleCategoryProblemListWithCategoryNull(title, memberId, category, pageable); // 1-2 검색 O 풀었는 문제 순 X
//            } else {
//                results = problemRepository.findAllTitleProblemList(title, memberId, pageable); // 1-3 검색 O 정답률 순 O
//            }
//        } else {
//            if ("Y".equals(category)) { // 2. 검색 조건이 있음
//                results = problemRepository.findAllCategoryProblemListWithCategoryY(memberId, category, pageable); // 2-1 검색 X 풀었는 문제 순 O
//            } else if ("N".equals(category)) {
//                results = problemRepository.findAllCategoryProblemListWithCategoryNull(memberId, category, pageable); // 2-2 검색 X 풀었는 문제 순 X
//            } else {
//                results = problemRepository.findAllProblemList(memberId, pageable); // 2-3 검색 X 정답률 순 O
//            }
//        }
//
//        List<Long> problemNos = results.stream()
//                .map(ProblemListDto::getProblemId)
//                .collect(Collectors.toList());
//        log.info("problemNos = {}", problemNos);
//
//        List<ProblemSolverDto> solvers = problemRepository.findSolversByProblemNos(problemNos, studyId);
//        log.info("solvers = {}", solvers);
//
//        Map<Long, List<String>> problemSolverMap = solvers.stream()
//                .collect(Collectors.groupingBy(ProblemSolverDto::getProblemNo,
//                        Collectors.mapping(ProblemSolverDto::getImgUrl, Collectors.toList())));
//        log.info("problemSolverMap = {}", problemSolverMap);
//
//        return results.stream()
//                .map(problem -> new ProblemListImgDto(
//                        problem.getProblemNo(),
//                        problem.getProblemId(),
//                        problem.getProblemTitle(),
//                        problem.getProblemLevel(),
//                        problem.getProblemLink(),
//                        problem.getStatus(),
//                        problemSolverMap.getOrDefault(problem.getProblemId(), Collections.emptyList())
//                ))
//                .collect(Collectors.toList());
//    }

    public ProblemListResponse getProblemList(String memberId, String studyId, String category, String title, int  page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ProblemListDslDto> results;
        // 1. 문제 가져오기
        results = problemRepository.findFilteredProblemList(title, category, memberId, pageable);

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

    public String getFormattedProblemTitle(String problemId) {
        ProblemEntity problem = problemRepository.findByProblemId(Long.parseLong(problemId))
                .orElseThrow(() -> new NoSuchElementException("문제를 찾을 수 없습니다: " + problemId));

        ProblemTitleDto dto = ProblemTitleDto.formatTitle(problem);
        return dto.formatTitle();
    }


}