package com.mildo.dev.api.problem.domain.dto;


import lombok.Data;

@Data
public class ProblemListDto {
    private Long problemNo;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private String problemType;
    private Long problemId;
}
