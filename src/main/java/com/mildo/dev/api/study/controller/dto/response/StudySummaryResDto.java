package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.domain.entity.StudyEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudySummaryResDto {

    private String code;
    private String name;
    private String startDate;
    private String endDate;

    public static StudySummaryResDto from(StudyEntity study) {
        return StudySummaryResDto.builder()
                .code(study.getStudyId())
                .name(study.getStudyName())
                .startDate(study.getStudyStart().toString())
                .endDate(study.getStudyEnd().toString())
                .build();
    }
}
