package com.podofarm.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class RecentActivityInfoDto {

    private String memberId;
    private String memberName;
    private Long problemId;
    private String problemTitle;
    private LocalDateTime solvedAt;

    @QueryProjection
    public RecentActivityInfoDto(String memberId, String memberName, Long problemId, String problemTitle, Timestamp solvedAt) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.solvedAt = solvedAt != null ? solvedAt.toLocalDateTime() : null;
    }
}
