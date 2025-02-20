package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.domain.entity.StudyEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Builder
public class StudyDetailResDto {

    private String studyId;
    private String studyName;
    private List<MemberDetailResDto> members;

    @Getter
    @Builder
    public static class MemberDetailResDto {
        private String id;
        private String name;
        private Boolean isLeader;
    }

    public static StudyDetailResDto from(StudyEntity study) {
        return StudyDetailResDto.builder()
                .studyId(study.getStudyId())
                .studyName(study.getStudyName())
                .members(study.getMemberEntityList().stream()
                        .map(member -> MemberDetailResDto.builder()
                                .id(member.getMemberId())
                                .name(member.getName())
                                .isLeader(member.getLeader().equals("Y"))
                                .build())
                        .collect(toList()))
                .build();
    }
}
