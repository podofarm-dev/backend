package com.mildo.dev.global.oauth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider implements JwtInterface{

    public static final String SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    public static final String REFRESH_TOKEN_SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());

    @Override
    public String getAccess(String userName) {
        return Jwts.builder()
                .setSubject(userName) // userName을 subject로 설정
//                .claim("username", user.getUserName()) // 추가 정보 저장
                .setIssuedAt(new Date()) // 발급 시간 (현재 시간으로 자동 설정)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1시간 후 만료
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명
                .compact();
    }

    @Override
    public Timestamp getAccessExpiration(String AccessToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // 서명 검증을 위한 키
                .parseClaimsJws(AccessToken) // 토큰 파싱
                .getBody(); // Payload 추출

        Date expirationDate = claims.getExpiration(); // 만료 시간 (Date 타입)
        return new Timestamp(expirationDate.getTime()); // Date를 Timestamp로 변환하여 반환
    }

    @Override
    public String getRefresh(String userName) {
        return Jwts.builder()
                .setSubject(userName) // 사용자 userName을 subject로 설정
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7일 후 만료
                .signWith(SignatureAlgorithm.HS512, REFRESH_TOKEN_SECRET_KEY) // 서명
                .compact();
    }

    @Override
    public Timestamp getRefreshExpiration(String RefreshToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(REFRESH_TOKEN_SECRET_KEY) // 서명 검증을 위한 키
                .parseClaimsJws(RefreshToken) // 토큰 파싱
                .getBody(); // Payload 추출

        Date expirationDate = claims.getExpiration();
        return new Timestamp(expirationDate.getTime());
    }
}
