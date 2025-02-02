package com.mildo.dev.api.code.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeLevelDTO {

    private String problemLevel;
    private Long problemCount;

}
