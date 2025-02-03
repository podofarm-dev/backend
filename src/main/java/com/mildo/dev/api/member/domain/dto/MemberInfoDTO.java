package com.mildo.dev.api.member.domain.dto;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {

    private String memberId;
    private String name;
    private String email;
    private String studyId;
    private String imgUrl;

}
