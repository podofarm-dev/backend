package com.mildo.dev.api.member.domain.dto.response;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {

    private String memberId;
    private String name;
    private String email;
    private String studyId;
    private String imgUrl;

}
