package com.mildo.dev.api.code.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UploadDTO {
    private String memberId;
    private String problemId;
    private LocalDateTime solvedDate;
    private String source;
    private String time;
    private String performance;
    private String annotation;
    private String status;
    private String annotatedSource;

    public UploadDTO(JsonNode request) {
        this.memberId = request.get("id").asText();
        this.problemId = request.get("problemId").asText();
        this.source = request.get("sourceText").asText();
        this.time = request.get("timeSpent").asText();
        this.performance = request.get("commitMessage").asText();
        this.annotation = "/** 주석 공간 테스트 */";
        this.status = "Y";

        this.annotatedSource = annotation + "\n" + source;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            this.solvedDate = LocalDateTime.parse(request.get("dateInfo").asText(), formatter);
        } catch (Exception e) {
            this.solvedDate = LocalDateTime.now();
        }
    }

    public Timestamp getSolvedDateAsTimestamp() {
        return Timestamp.valueOf(solvedDate);
    }
}
