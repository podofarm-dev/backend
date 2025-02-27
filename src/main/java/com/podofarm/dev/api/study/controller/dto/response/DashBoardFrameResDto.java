package com.podofarm.dev.api.study.controller.dto.response;

import com.podofarm.dev.api.study.repository.dto.StudyInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Builder
public class DashBoardFrameResDto {

    private String studyId;
    private String studyName;
    private Long lapsedDate;
    private Integer memberCount;
    private List<MemberResDto> memberDetails;

    @Getter
    @Builder
    public static class MemberResDto {
        private String id;
        private String name;
        private String imgUrl;
        private Boolean isLeader;
    }

    public static DashBoardFrameResDto fromRepoDto(StudyInfoDto repoDto) {
        return DashBoardFrameResDto.builder()
                .studyId(repoDto.getStudyId())
                .studyName(repoDto.getStudyName())
                .lapsedDate(TimeUnit.MILLISECONDS.toDays(
                        new Date().getTime() - repoDto.getStartDate().getTime()
                ))
                .memberCount(repoDto.getMembers().size())
                .memberDetails(
                        repoDto.getMembers().stream()
                                .map(member -> MemberResDto.builder()
                                        .id(member.getId())
                                        .name(member.getName())
                                        .imgUrl(member.getImgUrl())
                                        .isLeader("Y".equals(member.getLeader()))
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

}
