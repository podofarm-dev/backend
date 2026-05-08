package com.podofarm.dev.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8848").description("Local Server")
                ));
        // Security 설정 완전히 제거 - 개발 모드에서는 인증 불필요
    }

    private Info apiInfo() {
        return new Info()
                .title("Podofarm API")
                .description("포도팜 알고리즘 스터디 플랫폼 API 문서")
                .version("1.0.0");
    }
}
