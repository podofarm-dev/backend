package com.podofarm.dev.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResDto {

    private Integer code;
    private String message;

    public static ErrorResDto of(String message) {
        return new ErrorResDto(message);
    }

    public static ErrorResDto of(Integer code, String message) {
        return new ErrorResDto(code, message);
    }

    private ErrorResDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private ErrorResDto(String message) {
        this.message = message;
    }
}
