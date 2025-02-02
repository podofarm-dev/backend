package com.mildo.dev.api.member.domain.dto;

import com.mildo.dev.api.member.domain.entity.MemberImgEntity;
import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {

    private String memberId;
    private String studyId;
    private String name;
    private String email;
    private MemberImgEntity memberImgEntity;

}
