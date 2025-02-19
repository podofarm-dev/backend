package com.mildo.dev.api.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionSyncDTO {
    private String memberId;
    private String StudyId;
}
