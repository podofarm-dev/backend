package com.mildo.dev.api.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeSolvedListDTO {

    private Long problemNo;
    private String problemTitle;
    private String problemLevel;
    private String problemType;
    private Timestamp codeSolvedDate;
    private Time codeTime;
}
