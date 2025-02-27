package com.podofarm.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class GrassInfoDto {

    private String memberId;
    private Integer date;
    private Integer value;

    @QueryProjection
    public GrassInfoDto(String memberId, Integer date, Integer value) {
        this.memberId = memberId;
        this.date = date;
        this.value = value;
    }
}
