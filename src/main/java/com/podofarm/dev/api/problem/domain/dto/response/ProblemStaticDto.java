package com.podofarm.dev.api.problem.domain.dto.response;

import com.podofarm.dev.api.problem.domain.entity.ProblemEntity;
import lombok.*;

@Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ProblemStaticDto {
        private Long problemId;
        private String problemLevel;
        private String problemTitle;
        private String problemReadme;

        public static ProblemStaticDto formatTitle(ProblemEntity problem) {
            return ProblemStaticDto.builder()
                    .problemLevel(problem.getProblemLevel())
                    .problemTitle(problem.getProblemTitle())
                    .problemId(problem.getProblemId())
                    .problemReadme(problem.getProblemReadme()) // Readme 포함
                    .build();
        }

        public String getReadme() {
            return problemReadme;
        }

        public String formatTitle() {
            return String.format("[%s] %s - %s", this.problemLevel, this.problemTitle, this.problemId);
        }
    }


