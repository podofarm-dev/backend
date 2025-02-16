package com.mildo.dev.api.code.service;

import com.mildo.dev.api.code.domain.dto.response.CommentResponse;
import com.mildo.dev.api.code.domain.entity.CodeEntity;
import com.mildo.dev.api.code.domain.entity.CommentEntity;
import com.mildo.dev.api.code.repository.CodeRepository;
import com.mildo.dev.api.code.repository.CommentRepository;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.mildo.dev.global.exception.message.ExceptionMessage.MEMBER_NOT_FOUND_MSG;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final CommentRepository commentRepository;
    private final CodeRepository codeRepository;

    public List<CommentResponse> allComment(Long codeNo){
        checkCode(codeNo);
        List<CommentEntity> comments = commentRepository.findByCodeEntity_CodeNo(codeNo);

        return comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getCommentNo(),
                        comment.getCommentContent(),
                        comment.getCommentDate(),
                        comment.getMemberEntity().getMemberId(),
                        comment.getCodeEntity().getCodeNo()
                ))
                .collect(Collectors.toList());
    }

    public void checkCode(Long codeNo){
        CodeEntity code = codeRepository.findById(codeNo)
                .orElseThrow(() -> new RuntimeException("없는 코드입니다."));

    }

}
