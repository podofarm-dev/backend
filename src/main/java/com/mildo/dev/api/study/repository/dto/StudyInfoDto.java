package com.mildo.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class StudyInfoDto {

    private String studyId;
    private String studyName;
    private Date startDate;
    private List<MemberDto> members;

    @QueryProjection
    public StudyInfoDto(String studyId, String studyName, Date startDate, List<MemberDto> members) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.startDate = startDate;
        this.members = members;
    }

    @Getter
    public static class MemberDto {
        private String id;
        private String name;
        private String leader;

        @QueryProjection
        public MemberDto(String id, String name, String leader) {
            this.id = id;
            this.name = name;
            this.leader = leader;
        }
    }

}
