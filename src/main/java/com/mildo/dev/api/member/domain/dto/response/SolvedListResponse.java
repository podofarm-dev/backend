package com.mildo.dev.api.member.domain.dto.response;

import com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import lombok.Builder;
import lombok.Getter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SolvedListResponse {

    private List<SolvedDto> problemList;

    @Getter
    @Builder
    public static class SolvedDto {
        private Long problemNo;
        private String problemTitle;
        private String problemLevel;
        private String problemType;
        private Timestamp codeSolvedDate;
        private Time codeTime;
    }

    public static SolvedListResponse solvedDto(List<CodeSolvedListDTO> repoDto) {
        return SolvedListResponse.builder()
                .problemList(repoDto.stream()
                        .map(solved -> SolvedDto.builder()
                                .problemNo(solved.getProblemNo())
                                .problemTitle(solved.getProblemTitle())
                                .problemLevel(solved.getProblemLevel())
                                .problemType(solved.getProblemType())
                                .codeSolvedDate(solved.getCodeSolvedDate())
                                .codeTime(solved.getCodeTime())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
