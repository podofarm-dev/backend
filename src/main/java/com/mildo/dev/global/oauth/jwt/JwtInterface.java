package com.mildo.dev.global.oauth.jwt;

import java.sql.Timestamp;

public interface JwtInterface {

    String getAccess(String userName); // Access Token 생성

    Timestamp getAccessExpiration(String AccessToken); // Access Token 만료시간

    String getRefresh(String userName); // Refresh Token 생성

    Timestamp getRefreshExpiration(String RefreshToken); // Refresh Token 만료시간

}
