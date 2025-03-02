package com.podofarm.dev.global.OpenAI;

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

    public void printConfig() {
        System.out.println("🔍 OpenAI Model: " + model);
        System.out.println("🔍 OpenAI API Key: " + (apiKey != null ? "Loaded Successfully" : "Not Set"));
    }
}
