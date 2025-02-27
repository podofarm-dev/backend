package com.podofarm.dev.api.study.controller.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailySolvedSearchCond {

    private LocalDate date;
    private String member;

}
