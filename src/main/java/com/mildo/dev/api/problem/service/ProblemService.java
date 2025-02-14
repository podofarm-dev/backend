package com.mildo.dev.api.problem.service;

import com.mildo.dev.api.problem.domain.dto.response.ProblemListDto;
import com.mildo.dev.api.problem.domain.dto.response.ProblemListImgDto;
import com.mildo.dev.api.problem.domain.dto.response.ProblemSolverDto;
import com.mildo.dev.api.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public List<ProblemListImgDto> getProblemList(String memberId, String studyId, String category, String title, int  page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ProblemListDto> results;
        if (title != null && !title.isEmpty()) { // 1. 검색 조건이 있음
            if ("Y".equals(category)) {
                results = problemRepository.findAllTitleCategoryProblemListWithCategoryY(title, memberId, category, pageable); // 1-1 검색 O 풀었는 문제 순 O
            } else if ("N".equals(category)) {
                results = problemRepository.findAllTitleCategoryProblemListWithCategoryNull(title, memberId, category, pageable); // 1-2 검색 O 풀었는 문제 순 X
            } else {
                results = problemRepository.findAllTitleProblemList(title, memberId, pageable); // 1-3 검색 O 정답률 순 O
            }
        } else {
            if ("Y".equals(category)) { // 2. 검색 조건이 있음
                results = problemRepository.findAllCategoryProblemListWithCategoryY(memberId, category, pageable); // 2-1 검색 X 풀었는 문제 순 O
            } else if ("N".equals(category)) {
                results = problemRepository.findAllCategoryProblemListWithCategoryNull(memberId, category, pageable); // 2-2 검색 X 풀었는 문제 순 X
            } else {
                results = problemRepository.findAllProblemList(memberId, pageable); // 2-3 검색 X 정답률 순 O
            }
        }

        List<Long> problemNos = results.stream()
                .map(ProblemListDto::getProblemId)
                .collect(Collectors.toList());
        log.info("problemNos = {}", problemNos);

        List<ProblemSolverDto> solvers = problemRepository.findSolversByProblemNos(problemNos, studyId);
        log.info("solvers = {}", solvers);

        Map<Long, List<String>> problemSolverMap = solvers.stream()
                .collect(Collectors.groupingBy(ProblemSolverDto::getProblemNo,
                        Collectors.mapping(ProblemSolverDto::getImgUrl, Collectors.toList())));
        log.info("problemSolverMap = {}", problemSolverMap);

        return results.stream()
                .map(problem -> new ProblemListImgDto(
                        problem.getProblemNo(),
                        problem.getProblemId(),
                        problem.getProblemTitle(),
                        problem.getProblemLevel(),
                        problem.getProblemLink(),
                        problem.getStatus(),
                        problemSolverMap.getOrDefault(problem.getProblemId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

//    public List<ProblemListImgDto> getProblemList(String memberId, String studyId, String title, int  page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        List<ProblemListDto> results;
//        if (title != null && !title.isEmpty()) { // 검색 조건이 있을 때
//            results = problemRepository.findAllTitleProblemList(title, memberId, pageable);
//        } else {
//            results = problemRepository.findAllProblemList(memberId, pageable);
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
//                        problem.getProblemId(),
//                        problem.getProblemTitle(),
//                        problem.getProblemLevel(),
//                        problem.getProblemLink(),
//                        problem.getStatus(),
//                        problemSolverMap.getOrDefault(problem.getProblemId(), Collections.emptyList())
//                ))
//                .collect(Collectors.toList());
//    }
}