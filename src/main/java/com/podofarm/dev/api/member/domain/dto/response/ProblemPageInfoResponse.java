package com.podofarm.dev.api.member.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemPageInfoResponse {

    private String name;
    private String memberId;
    private String imgUrl;
    private Long solvedCount;


}
