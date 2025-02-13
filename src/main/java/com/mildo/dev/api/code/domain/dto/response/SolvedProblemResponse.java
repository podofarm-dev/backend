package com.mildo.dev.api.code.domain.dto.response;

import com.mildo.dev.api.code.domain.dto.request.CodeLevelDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SolvedProblemResponse {

    private List<CodeLevelDTO> problems;

}
