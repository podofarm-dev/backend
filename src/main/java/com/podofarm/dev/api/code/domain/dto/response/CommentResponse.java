package com.podofarm.dev.api.code.domain.dto.response;

import com.podofarm.dev.api.code.domain.entity.CommentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private Long commentNo;
    private String commentContent;
    private Timestamp commentDate;
    private String memberId;
    private Long codeNo;

    public CommentResponse(CommentEntity commentEntity) {
        this.commentNo = commentEntity.getCommentNo();
        this.commentContent = commentEntity.getCommentContent();
        this.commentDate = commentEntity.getCommentDate();
        this.memberId = commentEntity.getMemberEntity().getMemberId();
        this.codeNo = commentEntity.getCodeEntity().getCodeNo();
    }

}
