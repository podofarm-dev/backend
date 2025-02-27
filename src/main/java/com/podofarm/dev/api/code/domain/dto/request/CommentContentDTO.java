package com.podofarm.dev.api.code.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentContentDTO {

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String commentContent;
}
