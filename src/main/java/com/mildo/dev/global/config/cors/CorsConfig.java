package com.mildo.dev.global.config.cors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Slf4j
@Configuration
public class CorsConfig {

    // Cors 직접 사용
    @Bean
    public CorsFilter corsFitter(){
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
                                                "https://dev.mildo.xyz",
                                                "chrome-extension://magnaalaamndcofdpgeicpnlpdjajbjb")
                                                );

        config.addAllowedHeader("*"); // 모든 헤데어 응답을 허용
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
