package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.repository.dto.ProblemInfoDto;
import com.mildo.dev.api.study.service.utils.DashBoardUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailySolvedResDto {

    private List<SolvedProblemResDto> data;

    @Getter
    @Builder
    public static class SolvedProblemResDto {
        private Long id;
        private String title;
        private Integer level;
        private String type;
    }

    public static DailySolvedResDto fromRepoDto(List<ProblemInfoDto> repoDto) {
        List<SolvedProblemResDto> result = repoDto.stream()
                .map(aDto -> SolvedProblemResDto.builder()
                        .id(aDto.getId())
                        .title(aDto.getTitle())
                        .level(DashBoardUtils.getLevel(aDto.getLevel()))
                        .type(aDto.getType())
                        .build())
                .toList();

        return new DailySolvedResDto(result);
    }
}
