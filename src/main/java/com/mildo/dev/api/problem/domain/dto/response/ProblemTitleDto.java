package com.mildo.dev.api.problem.domain.dto.response;

import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemTitleDto {
    private String problemLevel;
    private String problemTitle;
    private Long problemId;

    public static ProblemTitleDto formatTitle(ProblemEntity problem) {
        return ProblemTitleDto.builder()
                .problemLevel(problem.getProblemLevel())
                .problemTitle(problem.getProblemTitle())
                .problemId(problem.getProblemId())
                .build();
    }

    public String formatTitle() {
        return String.format("[%s] %s - %s", this.problemLevel, this.problemTitle, this.problemId);
    }
}
