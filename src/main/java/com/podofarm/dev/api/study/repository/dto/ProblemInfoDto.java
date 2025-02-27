package com.podofarm.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ProblemInfoDto {

    private Long id;
    private String title;
    private String level;
    private String type;

    @QueryProjection
    public ProblemInfoDto(Long id, String title, String level, String type) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.type = type;
    }
}
