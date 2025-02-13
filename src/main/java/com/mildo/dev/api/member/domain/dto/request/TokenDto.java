package com.mildo.dev.api.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TokenDto {

    private String memberId;
    private String accessToken;
    private String refreshToken;


}
