package com.podofarm.dev.api.problem.repository.dto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ProblemListDslDto {

    private Long problemNo;
    private Long problemId;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private Boolean status;

    @QueryProjection
    public ProblemListDslDto(Long problemNo, Long problemId, String problemTitle, String problemLevel, String problemLink, Boolean status) {
        this.problemNo = problemNo;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.problemLevel = problemLevel;
        this.problemLink = problemLink;
        this.status = status;
    }
}
