package com.mildo.dev.api.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {

    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // accessToken 저장
    public static void saveAccessToken(String userId, String accessToken, long accessTokenTTL) {
        redisTemplate.opsForValue().set("accessToken:" + userId, accessToken, Duration.ofSeconds(accessTokenTTL));
    }

    // accessToken 조회
    public static String getAccessToken(String userId) {
        return (String) redisTemplate.opsForValue().get("accessToken:" + userId);
    }

    // accessToken TTL 조회
    public static Long getTTLAccess(String key) {
        return redisTemplate.getExpire("accessToken:" + key);
    }

    // accessToken TTL 갱신
    public static void setTTLAccess(String key, long ttlInSeconds) {
        redisTemplate.expire("accessToken:" + key, Duration.ofSeconds(ttlInSeconds));
    }

    // accessToken 삭제
    public static void deleteDataAccess(String key) {
        redisTemplate.delete("accessToken:" + key);
    }
}
