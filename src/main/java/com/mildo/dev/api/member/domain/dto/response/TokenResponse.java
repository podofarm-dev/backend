package com.mildo.dev.api.member.domain.dto.response;

import lombok.Data;

@Data
public class TokenResponse {

    private String memberId;
    private String accessToken;

    public TokenResponse(String memberId, String accessToken) {
        this.memberId = memberId;
        this.accessToken = accessToken;
    }

}
