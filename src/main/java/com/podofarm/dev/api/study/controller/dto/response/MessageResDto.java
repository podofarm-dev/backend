package com.podofarm.dev.api.study.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResDto {

    private String message;

    public static MessageResDto success(String message) {
        return MessageResDto.builder()
                .message(message)
                .build();
    }

}
