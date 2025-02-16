package com.mildo.dev.api.problem.domain.dto.response;

import com.mildo.dev.api.problem.repository.dto.ProblemListDslDto;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProblemListResponse {

    private final List<ProblemList> problem;

    @Getter
    @Builder
    public static class ProblemList {
        private Long problemNo;
        private Long problemId;
        private String problemTitle;
        private String problemLevel;
        private String problemLink;
        private String status;
        private List<String> img;
    }

    public static ProblemListResponse problemDto(List<ProblemListDslDto> res, Map<Long, List<String>> problemSolverMap) {
        return ProblemListResponse.builder()
                .problem(res.stream()
                        .map(p -> ProblemList.builder()
                                .problemNo(p.getProblemNo())
                                .problemId(p.getProblemId())
                                .problemTitle(p.getProblemTitle())
                                .problemLevel(p.getProblemLevel())
                                .problemLink(p.getProblemLink())
                                .status(p.getStatus())
                                .img(problemSolverMap.getOrDefault(p.getProblemId(), Collections.emptyList()))
                                .build()
                        )
                        .collect(Collectors.toList())
                ).build();
    }
}
