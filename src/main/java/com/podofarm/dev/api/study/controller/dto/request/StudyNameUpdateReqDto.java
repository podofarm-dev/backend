package com.podofarm.dev.api.study.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyNameUpdateReqDto {

    @NotBlank(message = "스터디명은 필수 입력값입니다.")
    @Size(max = 20, message = "스터디명은 최대 20글자까지 허용됩니다.")
    private String name;

}
