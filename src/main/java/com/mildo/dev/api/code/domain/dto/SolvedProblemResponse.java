package com.mildo.dev.api.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SolvedProblemResponse {

    private List<CodeLevelDTO> problems;

}
