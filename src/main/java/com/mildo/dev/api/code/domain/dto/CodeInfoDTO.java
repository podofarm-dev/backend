package com.mildo.dev.api.code.domain.dto;

import com.mildo.dev.api.code.domain.entity.CodeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.sql.Time;

@Getter
@Setter
@Builder
public class CodeInfoDTO {
    private String codeSource;

    @CreationTimestamp
    private Timestamp codeSolvedDate;
    private String codeStatus;
    private Time codeTime;
    private String codePerformance;
    private String codeAccuracy;
    private String problemType;  // ✅ 추가

    public static CodeInfoDTO fromEntity(CodeEntity code) {
        return CodeInfoDTO.builder()
                .codeSource(code.getCodeSource())
                .codeSolvedDate(code.getCodeSolvedDate())
                .codeStatus(code.getCodeStatus())
                .codeTime(code.getCodeTime())
                .codePerformance(code.getCodePerformance())
                .codeAccuracy(code.getCodeAccuracy())
                .problemType(code.getProblemEntity().getProblemType())
                .build();
    }
}
