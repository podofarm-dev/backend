package com.mildo.dev.api.problem.repository.dto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class QProblemListDto {

    private Long problemId;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private String status;

    @QueryProjection
    public QProblemListDto(Long problemId, String problemTitle, String problemLevel, String problemLink, String status) {
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.problemLevel = problemLevel;
        this.problemLink = problemLink;
        this.status = status;
    }
}
