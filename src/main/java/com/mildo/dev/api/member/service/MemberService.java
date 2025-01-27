package com.mildo.dev.api.member.service;

import com.mildo.dev.api.member.domain.dto.TokenDto;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.domain.entity.TokenEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.member.repository.TokenRepository;
import com.mildo.dev.global.exception.exceptionClass.TokenException;
import com.mildo.dev.global.oauth.jwt.JwtInterface;
import com.mildo.dev.global.oauth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtInterface jwtInterface;

    private static final String REFRESH_SECRET_KEY = JwtTokenProvider.REFRESH_TOKEN_SECRET_KEY;

    public TokenDto token(String memberId){
        String accessToken = jwtInterface.getAccess(memberId);
        String refreshToken = jwtInterface.getRefresh(memberId);
        Timestamp refreshTime = jwtInterface.getRefreshExpiration(refreshToken);

        // 나중에 멤버 없으면 예외 처리;
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException(""));

        Optional<TokenEntity> existingToken = tokenRepository.findByMemberEntity_MemberId(memberId);
        TokenEntity token;
        if (existingToken.isPresent()) {
            token = existingToken.get(); // 객체 가져오기
            token.setRefreshToken(refreshToken);
            token.setAccessToken(accessToken);
            token.setRefreshExpirationTime(refreshTime);
        } else {
            token = TokenEntity.builder()
                    .memberEntity(memberEntity) // memberEntity 관련 만 들어갈 수 있음
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .refreshExpirationTime(refreshTime)
                    .build();
        }

        tokenRepository.save(token);

        return new TokenDto(memberId, accessToken, refreshToken);
    }

    public String refreshNew(String RefreshToken){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .parseClaimsJws(RefreshToken)
                    .getBody();

            Optional<TokenEntity> isToken = tokenRepository.findByRefreshToken(RefreshToken); // DB에 토큰 조회
            String accessToken = jwtInterface.getAccess(claims.getSubject()); // 새로운 토큰 생성

            TokenEntity token;
            if(!isToken.isPresent()){
                throw new TokenException("DB Warning - Login Again");
            } else{
                token = isToken.get();
                token.setAccessToken(accessToken);
            }
            tokenRepository.save(token);

            return accessToken;

        } catch (ExpiredJwtException e) { // Token 만료 시 발생
            log.error("ExpiredJwtException e = {}", e.getMessage());
            throw new TokenException("expired - Login Again");
        }catch (Exception e) { // 유효하지 않으면
            log.error("Exception e = {}", e.getMessage());
            throw new TokenException("Faill - Login Again");
        }
    }

}
