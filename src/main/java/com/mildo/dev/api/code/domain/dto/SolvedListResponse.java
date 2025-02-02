package com.mildo.dev.api.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedListResponse {

    private List<CodeSolvedListDTO> problemList;
}
