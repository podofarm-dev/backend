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
                "\n 1. 전달받은 코드를정리하기. 반드시 // 붙여서 주석으로 처리\n" +
                "2. 주석은 한 공간에 몰아서 작성\n" +
                "3. 예시:\n" +
                "Open AI 정리" +
                "//01 내용1\n" +
                "//02 내용2\n" +
                "//03 내용3\n" +
                "4. 간단한 문장으로 설명\n" +
                "5. 60 글자가 넘으면 줄바꿈\n" +
                "\n";
    }
}
