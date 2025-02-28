package com.podofarm.dev.api.problem.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProblemSolverDto {

    private Long problemNo;
    private String imgUrl;
    private String name;
}
