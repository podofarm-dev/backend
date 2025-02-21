package com.mildo.dev.api.member.domain.dto.response;

import com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SolvedListResponse {

    private List<SolvedDto> problemList;
    private Pageable PageInfo;

    @Getter
    @Builder
    public static class SolvedDto {
        private Long problemNo;
        private Long problemId;
        private String problemTitle;
        private String problemLevel;
        private String problemType;
        private Timestamp codeSolvedDate;
        private Time codeTime;

        public String getProblemLevel() {
            return problemLevel.replaceAll("\\D+", "");
        }
    }

    @Getter
    @Builder
    public static class Pageable {
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int size;
    }

    public static SolvedListResponse solvedDto(Page<CodeSolvedListDTO> repoDto) {
        return SolvedListResponse.builder()
                .problemList(repoDto.stream()
                        .map(solved -> SolvedDto.builder()
                                .problemNo(solved.getProblemNo())
                                .problemId(solved.getProblemId())
                                .problemTitle(solved.getProblemTitle())
                                .problemLevel(solved.getProblemLevel())
                                .problemType(solved.getProblemType())
                                .codeSolvedDate(solved.getCodeSolvedDate())
                                .codeTime(solved.getCodeTime())
                                .build())
                        .collect(Collectors.toList())
                )
                .PageInfo(Pageable.builder()
                        .totalElements(repoDto.getTotalElements())
                        .totalPages(repoDto.getTotalPages())
                        .currentPage(repoDto.getNumber())
                        .size(repoDto.getSize())
                        .build())
                .build();
    }
}
