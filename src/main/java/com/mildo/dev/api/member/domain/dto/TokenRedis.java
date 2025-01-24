package com.mildo.dev.api.member.domain.dto;

import lombok.Data;

@Data
public class TokenRedis {

    private String memberId;
    private String accessToken;

    public TokenRedis(String memberId, String accessToken) {
        this.memberId = memberId;
        this.accessToken = accessToken;
    }

}
