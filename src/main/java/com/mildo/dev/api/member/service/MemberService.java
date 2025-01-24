package com.mildo.dev.api.member.service;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.member.domain.dto.TokenRedis;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.domain.entity.TokenEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.member.repository.TokenRepository;
import com.mildo.dev.api.utils.redis.RedisUtil;
import com.mildo.dev.global.oauth.jwt.JwtInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
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

    public TokenRedis token(String memberId){
        String accessToken = jwtInterface.getAccess(memberId);
        String refreshToken = jwtInterface.getRefresh(memberId);
        Timestamp refreshTime = jwtInterface.getRefreshExpiration(refreshToken);

        // 나중에 멤버 없으면 예외 처리;
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

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
        return new TokenRedis(memberId, accessToken);
    }

}
