package com.mildo.dev.global.oauth.jwt;

import java.sql.Timestamp;

public interface JwtInterface {

    String getAccess(String memberId); // Access Token 생성

    Timestamp getAccessExpiration(String AccessToken); // Access Token 만료시간

    String getRefresh(String memberId); // Refresh Token 생성

    Timestamp getRefreshExpiration(String RefreshToken); // Refresh Token 만료시간

}
