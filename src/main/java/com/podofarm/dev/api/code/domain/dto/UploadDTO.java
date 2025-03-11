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
    private Timestamp solvedDate;
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

        try {
            String resultDay = request.get("resultDay").asText();
            System.out.println("resultDay 출력"  + resultDay);
            this.solvedDate = Timestamp.valueOf(resultDay);
            System.out.println("solvedDate 출력" + solvedDate);
        } catch (Exception e) {
            this.solvedDate = new Timestamp(System.currentTimeMillis());
        }
    }

    public CodeEntity insertCodeEntity(MemberEntity member, ProblemEntity problem) {
        return CodeEntity.builder()
                .memberEntity(member)
                .problemEntity(problem)
                .codeSource("분석 중...\n\n" + source)
                .codeSolvedDate(this.solvedDate)
                .codeTime(Time.valueOf(this.time))
                .codeStatus(this.status)
                .codePerformance(this.performance)
                .codeAccuracy(this.accuracy)
                .build();
    }

    public void updateCodeEntity(CodeEntity updateCode) {
        updateCode.setCodeSource("분석 중...\n\n" + this.source);
        updateCode.setCodeSolvedDate(this.solvedDate);
        updateCode.setCodeTime(Time.valueOf(this.time));
        updateCode.setCodePerformance(this.performance);
        updateCode.setCodeAccuracy(this.accuracy);
    }
}
