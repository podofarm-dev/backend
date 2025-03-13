package com.podofarm.dev.api.problem.domain.dto.response;

import com.podofarm.dev.api.problem.domain.dto.request.UserProfileDto;
import com.podofarm.dev.api.problem.repository.dto.ProblemListDslDto;
import com.podofarm.dev.api.study.service.utils.DashBoardUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProblemListResponse {

    private final List<ProblemList> problem;
    private Pageable PageInfo;

    @Getter
    @Builder
    public static class ProblemList {
        private Long problemNo;
        private Long problemId;
        private String problemTitle;
        private Integer problemLevel;
        private String problemLink;
        private Boolean status;
        private List<UserProfileDto> img;
    }

    @Getter
    @Builder
    public static class Pageable {
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int size;
    }

    public static ProblemListResponse problemDto(Page<ProblemListDslDto> res, Map<Long, List<UserProfileDto>> problemSolverMap) {
        return ProblemListResponse.builder()
                .problem(res.stream()
                        .map(p -> ProblemList.builder()
                                .problemNo(p.getProblemNo())
                                .problemId(p.getProblemId())
                                .problemTitle(p.getProblemTitle())
                                .problemLevel(DashBoardUtils.getLevel(p.getProblemLevel()))
                                .problemLink(p.getProblemLink())
                                .status(p.getStatus())
                                .img(problemSolverMap.getOrDefault(p.getProblemId(), Collections.emptyList()))
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .PageInfo(ProblemListResponse.Pageable.builder()
                        .totalElements(res.getTotalElements())
                        .totalPages(res.getTotalPages())
                        .currentPage(res.getNumber())
                        .size(res.getSize())
                        .build())
                .build();
    }


}
