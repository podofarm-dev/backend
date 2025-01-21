package com.mildo.dev.api.member.domain.dto;

import lombok.Data;

@Data
public class TokenRedis {

    private String userId;
    private String accessToken;

    public TokenRedis(String userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

}
