package com.mildo.dev.api.problem.domain.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProblemListDto {

    private Long problemNo;
    private Long problemId;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private Boolean status;
}
