package com.mildo.dev.api.user.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenDto {

    private int userid;
    private String accessToken;
    private LocalDateTime accessExpirationTime;
    private String refreshToken;
    private LocalDateTime refreshExpirationTime;

    public TokenDto(){};
}
