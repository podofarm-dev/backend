package com.podofarm.dev.api.code.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.podofarm.dev.api.code.domain.entity.CodeEntity;
import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import com.podofarm.dev.api.problem.domain.entity.ProblemEntity;
import lombok.Getter;

import java.sql.Time;
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
    private boolean status;
    private String annotatedSource;
    private String accuracy;

    public UploadDTO(JsonNode request) {
        this.memberId = request.get("id").asText();
        this.problemId = request.get("problemId").asText();
        this.source = request.get("sourceText").asText();
        this.time = request.get("timeSpent").asText();
        this.performance = request.get("commitMessage").asText();
        this.annotation = "/** 주석 공간 테스트 */";
        this.status = true;
        this.annotatedSource = annotation + "\n" + source;
        this.accuracy = request.get("resultMessage").asText();

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

    public CodeEntity insertCodeEntity(MemberEntity member, ProblemEntity problem) {
        return CodeEntity.builder()
                .memberEntity(member)
                .problemEntity(problem)
                .codeSource("분석 중...")
                .codeSolvedDate(getSolvedDateAsTimestamp())
                .codeTime(Time.valueOf(this.time))
                .codeStatus(this.status)
                .codePerformance(this.performance)
                .codeAccuracy(this.accuracy)
                .build();
    }

    public void updateCodeEntity(CodeEntity updateCode) {
        updateCode.setCodeSource(this.source);
        updateCode.setCodeSolvedDate(getSolvedDateAsTimestamp());
        updateCode.setCodeTime(Time.valueOf(this.time));
        updateCode.setCodePerformance(this.performance);
        updateCode.setCodeAccuracy(this.accuracy);
    }
}
