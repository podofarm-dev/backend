package com.mildo.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CountingSolvedDto {

    private String memberId;
    private Integer solved;

    @QueryProjection
    public CountingSolvedDto(String memberId, Integer solved) {
        this.memberId = memberId;
        this.solved = solved;
    }
}
