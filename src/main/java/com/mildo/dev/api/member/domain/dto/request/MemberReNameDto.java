package com.mildo.dev.api.member.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberReNameDto {

    @Size(min = 1, max = 8, message = "이름은 1~8글자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "이름에는 특수문자를 포함할 수 없습니다.")
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;
}
