package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.repository.dto.RecentActivityInfoDto;
import com.mildo.dev.api.study.service.utils.DashBoardUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LogResDto {

    private List<RecentActivityResDto> data;

    @Getter
    @Builder
    public static class RecentActivityResDto {

        private String memberId;
        private String memberName;
        private Long problemId;
        private String problemTitle;
        private String solvedBefore;
    }

    public static LogResDto fromRepoDto(List<RecentActivityInfoDto> repoDto, LocalDateTime now) {
        List<RecentActivityResDto> result = repoDto.stream()
                .map(aDto -> RecentActivityResDto.builder()
                        .memberId(aDto.getMemberId())
                        .memberName(aDto.getMemberName())
                        .problemId(aDto.getProblemId())
                        .problemTitle(aDto.getProblemTitle())
                        .solvedBefore(DashBoardUtils.getSolvedBefore(aDto.getSolvedAt(), now))
                        .build())
                .toList();

        return new LogResDto(result);
    }
}

