package com.mildo.dev.api.problem.domain.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProblemListDto {

//    private Long problemNo;
    private Long problemId;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private String status;
}
