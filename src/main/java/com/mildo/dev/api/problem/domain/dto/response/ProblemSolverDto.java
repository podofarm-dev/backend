package com.mildo.dev.api.problem.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProblemSolverDto {

    private Long problemNo;
    private String imgUrl;
}
