package com.mildo.dev.api.problem.domain.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProblemListImgDto {

    private Long problemId;
    private String problemTitle;
    private String problemLevel;
    private String problemLink;
    private String status;
    private List<String> img;

}
