package com.podofarm.dev.api.study.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyCreateReqDto {

    @NotBlank(message = "스터디명은 필수 입력값입니다.")
    @Size(max = 20, message = "스터디명은 최대 20글자까지 허용됩니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(max = 8, message = "비밀번호는 최대 8글자까지 허용됩니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$",
            message = "비밀번호는 알파벳과 숫자만 허용되며, 각각 최소 1개 이상 포함해야 합니다."
    )
    private String password;

}
