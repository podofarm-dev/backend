package com.podofarm.dev.global.config.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-key}")
    private String apiKey;

    public String getModel() {
        return model;
    }

    public String getApiKey() {
        return apiKey;
    }
}
