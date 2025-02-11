package com.mildo.dev.api.study.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyJoinReqDto {

    @NotBlank(message = "스터디 코드는 필수 입력값입니다.")
    private String code;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

}
