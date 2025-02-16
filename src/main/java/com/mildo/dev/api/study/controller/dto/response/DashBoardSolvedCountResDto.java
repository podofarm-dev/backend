package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.repository.dto.CountingSolvedDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DashBoardSolvedCountResDto {

    private List<MemberSolvedCountResDto> data;

    @Getter
    @Builder
    public static class MemberSolvedCountResDto {
        private String memberId;
        private Integer solved;
    }

    public static DashBoardSolvedCountResDto fromRepoDto(List<CountingSolvedDto> repoDto) {
        return DashBoardSolvedCountResDto.builder()
                .data(repoDto.stream()
                        .map(aDto -> MemberSolvedCountResDto.builder()
                                .memberId(aDto.getMemberId())
                                .solved(aDto.getSolved())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }


}
