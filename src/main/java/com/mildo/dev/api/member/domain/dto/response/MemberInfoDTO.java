package com.mildo.dev.api.member.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
