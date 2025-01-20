package com.mildo.dev.api.user.domain.dto;

import lombok.Data;

@Data
public class TokenRedis {

    private String id;
    private String accessToken;

    public TokenRedis(String id, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;
    }

}
