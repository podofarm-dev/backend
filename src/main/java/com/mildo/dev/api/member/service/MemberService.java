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

    public TokenRedis token(CustomUser customUser){
        String accessToken = null;
        String refreshToken = null;
        Timestamp refreshTime = null;

        try {
            refreshToken = jwtInterface.getRefresh(customUser.getName());
            refreshTime = jwtInterface.getRefreshExpiration(refreshToken);
            log.info("refreshTime = {}", refreshTime);

            accessToken = jwtInterface.getAccess(customUser.getName());
            log.info("customUser.getName() = {}", customUser.getName());
            log.info("accessToken = {}", accessToken);

            RedisUtil.saveAccessToken(customUser.getName(), accessToken, 60); // accessToken 저장
            String getAccess = RedisUtil.getAccessToken(customUser.getName()); // accessToken 값 조회
            log.info("getAccess = {}", getAccess);

            Long getTTL = RedisUtil.getTTLAccess(customUser.getName()); // TTL 시간 확인
            log.info("getTTL = {}", getTTL);

        } catch (RedisConnectionFailureException e){
            log.error("Redis 서버 연결 실패: ", e);
            throw new RedisConnectionFailureException("Redis 문제");
        } catch (Exception e) {
            log.error("Redis 작업 중 오류가 발생했습니다: ", e);
        }

        MemberEntity memberEntity = memberRepository.findById(customUser.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Optional<TokenEntity> existingToken = tokenRepository.findByMemberEntity_MemberId(customUser.getName());
        TokenEntity token;
        if (existingToken.isPresent()) {
            token = existingToken.get(); // 객체 가져오기
            token.setRefreshToken(refreshToken);
            token.setRefreshExpirationTime(refreshTime);
        } else {
             token = TokenEntity.builder()
                    .memberEntity(memberEntity) // memberEntity 관련 만 들어갈 수 있음
                    .refreshToken(refreshToken)
                    .refreshExpirationTime(refreshTime)
                    .build();
        }

        tokenRepository.save(token);

        return new TokenRedis(customUser.getName(), accessToken);
    }

}
