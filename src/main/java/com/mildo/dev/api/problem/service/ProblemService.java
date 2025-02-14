package com.mildo.dev.api.problem.service;

import com.mildo.dev.api.problem.domain.dto.ProblemListDto;
import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import com.mildo.dev.api.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public List<ProblemListDto> getProblemList() {
        List<Object[]> results = problemRepository.findAllProblemList();
        List<ProblemListDto> problemList = new ArrayList<>();

        for (Object[] row : results) {
            ProblemListDto dto = new ProblemListDto();
            dto.setProblemNo((Long) row[0]);
            dto.setProblemTitle((String) row[1]);
            dto.setProblemLevel((String) row[2]);
            dto.setProblemLink((String) row[3]);
            dto.setProblemType((String) row[4]);
            dto.setProblemId((Long) row[5]);

            problemList.add(dto);
        }
        return problemList;
    }

}