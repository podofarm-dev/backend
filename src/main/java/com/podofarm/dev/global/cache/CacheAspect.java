package com.podofarm.dev.global.cache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;
import com.podofarm.dev.global.cache.CacheProblemList;
@Aspect
@Component
@Slf4j
public class CacheAspect {
    private final CacheManager cacheManager;

    public CacheAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Around("@annotation(CacheProblemList)")
    public Object cacheProblemList(ProceedingJoinPoint joinPoint) throws Throwable {
        return handleCaching(joinPoint, "Cache-problemList");
    }

    private Object handleCaching(ProceedingJoinPoint joinPoint, String cacheName) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String key = (String) args[0];
        System.out.println("매개변수 캐시 확인" + key);

        var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return joinPoint.proceed();
        }

        var cachedData = cache.get(key);
        if (cachedData != null) {
            return Objects.requireNonNull(cachedData.get());
        }

        Object result = joinPoint.proceed();

        if (result != null) {
            cache.put(key, result);
        }

        return result;
    }
}
