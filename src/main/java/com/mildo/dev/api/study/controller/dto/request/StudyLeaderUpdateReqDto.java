package com.mildo.dev.api.study.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyLeaderUpdateReqDto {

    @NotBlank(message = "스터디장 ID는 필수 입력값입니다.")
    private String leaderId;

}
