package com.podofarm.dev.global.OpenAI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(OpenAIConfig openAiConfig) {
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + openAiConfig.getApiKey())  // API 키 설정
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}