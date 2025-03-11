package com.podofarm.dev.api.code.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Getter
    @Setter
    public static class Choice {
        private int index;
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Getter
    @Setter
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Setter
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;

        @JsonProperty("completion_tokens")
        private int completionTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;
    }

    public String getAnalyzedCode() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent(); // 첫 번째 응답의 content 반환
        }
        return ""; // choices가 비어있으면 빈 문자열 반환
    }

    public static String getPrompt(String code) {
        return  code +
                "1. Summarize the methods used. " +
                "2. Add comments before each method In Korea, such as // String.valueOf 사용, // dp[]배열 선언. " +
                "3. Automatically format the code for better readability. " +
                "\n";

    }
}
