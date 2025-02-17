package com.mildo.dev.api.code.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadDTO {
    private String memberId;
    private String problemId;
    private String codeSource;
    private String codeSolvedDate;
    private String codeAnnotation;
    private String codeStatus;
    private String codeTime;
    private String problemTitle;

}
