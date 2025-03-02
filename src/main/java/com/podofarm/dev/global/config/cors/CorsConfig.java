package com.podofarm.dev.global.config.cors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Slf4j
@Configuration
public class CorsConfig {

    @Value("${domain.front}")
    private String frontDomain;

    // Cors 직접 사용
    @Bean
    public CorsFilter corsFitter(){
        log.info("frontDomain={}", frontDomain);

        log.info("================== Cors 필터1 시작 ========================");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 자바스크립트로 받을 수 있게 할건지

        // IP 출처가 달라도 응답을 허용
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                                                "https://localhost:3000",
                                                "http://localhost:5173",
                                                "http://localhost:5174",
                                                "http://localhost:5175",
                                                "https://school.programmers.co.kr",
                                                "https://" + frontDomain,
                                                "https://www." + frontDomain,
                                                "https://test.mildo.xyz/*",
                                                "chrome-extension://magnaalaamndcofdpgeicpnlpdjajbjb",
                                                "chrome-extension://bcbabakaolnokikhllajhgchlgeiihld")
                                                );

        config.addAllowedHeader("*"); // 모든 헤데어 응답을 허용
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
