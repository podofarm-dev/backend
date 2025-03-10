package com.podofarm.dev.global.OpenAI;

import com.podofarm.dev.api.code.domain.dto.response.OpenAIResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.podofarm.dev.global.config.openai.OpenAIConfig;
import java.util.List;
import java.util.Map;

@Component
public class OpenAIClient {

    private final WebClient webClient;
    private final OpenAIConfig openAiConfig;

    public OpenAIClient(OpenAIConfig openAiConfig) {
        this.openAiConfig = openAiConfig;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + openAiConfig.getApiKey())  // API 키 설정
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public OpenAIResponse sendRequestToOpenAI(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", openAiConfig.getModel(),
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.8
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();
    }

}
