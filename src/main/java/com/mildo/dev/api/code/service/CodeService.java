package com.mildo.dev.api.code.service;

import com.mildo.dev.api.code.domain.dto.CodeInfoDTO;
import com.mildo.dev.api.code.domain.dto.UploadDTO;
import com.mildo.dev.api.code.domain.dto.response.CommentResponse;
import com.mildo.dev.api.code.domain.dto.response.CommentListResponse;
import com.mildo.dev.api.code.domain.entity.CodeEntity;
import com.mildo.dev.api.code.domain.entity.CommentEntity;
import com.mildo.dev.api.code.repository.CodeRepository;
import com.mildo.dev.api.code.repository.CommentRepository;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.member.service.MemberService;
import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import com.mildo.dev.api.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.mildo.dev.api.code.domain.dto.UploadDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final CommentRepository commentRepository;
    private final CodeRepository codeRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;

    public void upload(JsonNode request) {
        UploadDTO uploadDTO = new UploadDTO(request);

        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(uploadDTO.getMemberId());
        if (memberEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다: " + uploadDTO.getMemberId());
        }
        MemberEntity memberEntity = memberEntityOptional.get();

        Optional<ProblemEntity> problemEntityOptional = problemRepository.findById(Long.parseLong(uploadDTO.getProblemId()));
        if (problemEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 문제 ID가 존재하지 않습니다: " + uploadDTO.getProblemId());
        }
        ProblemEntity problemEntity = problemEntityOptional.get();

        CodeEntity codeEntity = CodeEntity.builder()
                .memberEntity(memberEntity)
                .problemEntity(problemEntity)
                .codeSource(uploadDTO.getAnnotatedSource())
                .codeSolvedDate(uploadDTO.getSolvedDateAsTimestamp())
                .codeTime(Time.valueOf(uploadDTO.getTime()))
                .codeStatus(uploadDTO.getStatus())
                .build();

        codeRepository.save(codeEntity);
    }


    private String generateAnnotation(String code) {
        String apiKey = ""; // OpenAI API 키 설정
        String openAiUrl = "https://api.openai.com/v1/completions";

        return "코드 분석 실패: 기본 주석을 사용하세요.";
    }








    public CommentListResponse allComment(Long codeNo) {
        CodeEntity code = codeRepository.findByIdWithComments(codeNo)
                .orElseThrow(() -> new RuntimeException("없는 코드입니다."));

        List<CommentEntity> comments = code.getCommentList();

        return CommentListResponse.fromRepoDto(comments);
    }

    public CommentResponse insertComment(Long codeNo, String commentContent, String memberId) {
        CodeEntity code = checkCode(codeNo);
        MemberEntity member = memberService.vaildMemberId(memberId);

        CommentEntity comment = CommentEntity.builder()
                .codeEntity(code)
                .memberEntity(member)
                .commentContent(commentContent)
                .commentDate(new Timestamp(System.currentTimeMillis()))
                .build();

        CommentEntity res = commentRepository.save(comment);
        return new CommentResponse(res);
    }

    public void deleteComment(Long codeNo, Long commentNo, String memberId) {
        checkCode(codeNo);
        CommentEntity comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new RuntimeException("없는 댓글입니다."));

        if (!comment.getMemberEntity().getMemberId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    public CodeEntity checkCode(Long codeNo) {
        CodeEntity code = codeRepository.findById(codeNo)
                .orElseThrow(() -> new RuntimeException("없는 코드입니다."));
        return code;

    }

    public List<CodeInfoDTO> getMemberSolvedInfo(String memberId, Long problemId) {
        List<CodeEntity> codeEntities = codeRepository.findByMemberEntity_MemberIdAndProblemEntity_ProblemId(memberId, problemId);
        List<CodeInfoDTO> codeInfoList = new ArrayList<>();

        for (int i = 0; i < codeEntities.size(); i++)
            codeInfoList.add(CodeInfoDTO.fromEntity(codeEntities.get(i)));

        return codeInfoList;
    }

}