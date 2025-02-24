package com.mildo.dev.api.problem.domain.dto.request;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private String imgUrl;
    private String name;
}
