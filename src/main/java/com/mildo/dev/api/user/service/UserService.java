package com.mildo.dev.api.user.service;

import com.mildo.dev.api.user.customoauth.dto.CustomUser;
import com.mildo.dev.api.user.domain.dto.TokenRedis;
import com.mildo.dev.api.utils.redis.RedisUtil;
import com.mildo.dev.global.oauth.jwt.JwtInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtInterface jwtInterface;

    public TokenRedis token(CustomUser customUser){
        String accessToken = null;
        try {
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

        return new TokenRedis(customUser.getName(), accessToken);
    }

}
