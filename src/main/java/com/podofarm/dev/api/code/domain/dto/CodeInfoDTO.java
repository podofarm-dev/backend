package com.podofarm.dev.api.code.domain.dto;

import com.podofarm.dev.api.code.domain.entity.CodeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Time;

@Getter
@Setter
@Builder
public class CodeInfoDTO {
    private Long codeNo;
    private String codeSource;
    private String codeSolvedDate;
    private Boolean codeStatus;
    private Time codeTime;
    private String codePerformance;
    private String codeAccuracy;
    private String problemType;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

    public static CodeInfoDTO fromEntity(CodeEntity code) {
        return CodeInfoDTO.builder()
                .codeNo(code.getCodeNo())
                .codeSource(code.getCodeSource())
                .codeSolvedDate(formatTimestamp(code.getCodeSolvedDate()))
                .codeStatus(code.getCodeStatus())
                .codeTime(code.getCodeTime())
                .codePerformance(code.getCodePerformance())
                .codeAccuracy(code.getCodeAccuracy())
                .problemType(code.getProblemEntity().getProblemType())
                .build();
    }

    private static String formatTimestamp(java.sql.Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
