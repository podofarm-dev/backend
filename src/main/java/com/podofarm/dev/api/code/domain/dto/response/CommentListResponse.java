package com.podofarm.dev.api.code.domain.dto.response;

import com.podofarm.dev.api.code.domain.entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentListResponse {

    private List<CommentResponseDto> data;

    @Getter
    @Builder
    public static class CommentResponseDto {
        private Long commentNo;
        private String commentContent;
        private Timestamp commentDate;
        private String memberId;
        private Long codeNo;
    }

    public static CommentListResponse fromRepoDto(List<CommentEntity> repoDto) {
        return CommentListResponse.builder()
                .data(repoDto.stream()
                        .map(comment -> CommentResponseDto.builder()
                                .commentNo(comment.getCommentNo())
                                .commentContent(comment.getCommentContent())
                                .commentDate(comment.getCommentDate())
                                .memberId(comment.getMemberEntity().getMemberId())
                                .codeNo(comment.getCodeEntity().getCodeNo())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }

}
