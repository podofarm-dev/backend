package com.podofarm.dev.api.study.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CountingSolvedDto {

    private String memberId;
    private String name;
    private Integer solved;

    @QueryProjection
    public CountingSolvedDto(String memberId, String name, Integer solved) {
        this.memberId = memberId;
        this.name = name;
        this.solved = solved;
    }
}
