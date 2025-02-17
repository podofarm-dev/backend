package com.mildo.dev.api.member.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedMemberListDto {

    private String studyId;
    private String memberId;
    private String name;
    private int solvedProblem;
    private String imgUrl;
    private int rank;

    public void setRank(int rank) {
        this.rank = rank;
    }
}
