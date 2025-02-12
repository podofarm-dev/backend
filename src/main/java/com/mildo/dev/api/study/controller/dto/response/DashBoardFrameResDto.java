package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.repository.dto.StudyInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneId.systemDefault;

@Getter
@Builder
public class DashBoardFrameResDto {

    private String studyId;
    private String studyName;
    private String lapsedDate;
    private Integer memberCount;
    private List<MemberResDto> memberDetails;

    @Getter
    @Builder
    public static class MemberResDto {
        private String id;
        private String name;
        private Boolean isLeader;
    }

    public static DashBoardFrameResDto fromRepoDto(StudyInfoDto repoDto) {
        return DashBoardFrameResDto.builder()
                .studyId(repoDto.getStudyId())
                .studyName(repoDto.getStudyName())
                .lapsedDate(Period.between(
                        LocalDate.now(),
                        repoDto.getStartDate().toInstant().atZone(systemDefault()).toLocalDate())
                        .toString())
                .memberCount(repoDto.getMembers().size())
                .memberDetails(
                        repoDto.getMembers().stream()
                                .map(member -> MemberResDto.builder()
                                        .id(member.getId())
                                        .name(member.getName())
                                        .isLeader("Y".equals(member.getLeader()))
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

}
