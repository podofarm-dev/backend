package com.mildo.dev.api.code.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long commentNo;
    private String commentContent;
    private Timestamp commentDate;
    private String memberId;
    private Long codeNo;
}
