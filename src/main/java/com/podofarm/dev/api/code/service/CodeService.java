package com.podofarm.dev.api.code.service;

import com.podofarm.dev.api.code.domain.dto.CodeInfoDTO;
import com.podofarm.dev.api.code.domain.dto.UploadDTO;
import com.podofarm.dev.api.code.domain.dto.response.CommentListResponse;
import com.podofarm.dev.api.code.domain.dto.response.CommentResponse;
import com.podofarm.dev.api.code.domain.dto.response.OpenAIResponse;
import com.podofarm.dev.api.code.domain.entity.CodeEntity;
import com.podofarm.dev.api.code.domain.entity.CommentEntity;
import com.podofarm.dev.api.code.repository.CodeRepository;
import com.podofarm.dev.api.code.repository.CommentRepository;
import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import com.podofarm.dev.api.member.repository.MemberRepository;
import com.podofarm.dev.api.member.service.MemberService;
import com.podofarm.dev.api.problem.repository.ProblemRepository;
import com.podofarm.dev.global.OpenAI.OpenAIClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {
    private final CommentRepository commentRepository;
    private final CodeRepository codeRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final OpenAIClient openaiCient;

    private final Map<String, List<Long>> responseData = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Async("sync-extension")
    public void upload(UploadDTO request) {
        Long problemId = Long.valueOf(request.getProblemId());
        String memberId = request.getMemberId();
        Optional<CodeEntity> checkSolvedProblem = codeRepository.findByMemberIdAndProblemId(memberId, problemId);

        if (checkSolvedProblem.isPresent()) {
            CodeEntity updateCode = checkSolvedProblem.get();
            request.updateCodeEntity(updateCode);
            codeRepository.save(updateCode);
        } else {
            CodeEntity insertCode = request.insertCodeEntity(
                    memberRepository.getReferenceById(memberId),
                    problemRepository.getReferenceById(problemId));
            codeRepository.save(insertCode);
            memberRepository.incrementSolvedProblem(memberId);
        }
    }

    @Async("sync-code")
    public void openAI(String source, String memberId, String problemId) {
        OpenAIResponse responseAI = analyzeCode(source);
        updateSource(responseAI.getAnalyzedCode(), memberId, problemId);
    }

    public OpenAIResponse analyzeCode(String request) {
        return openaiCient.sendRequestToOpenAI(OpenAIResponse.getPrompt(request));
    }

    public void updateSource(String result, String memberId, String problemId) {
        String problemSolution = Optional.ofNullable(problemRepository.findSolutionByProblemId(Long.valueOf(problemId)))
                .orElse("");

        codeRepository.updateCodeSource(problemSolution + "\n\n" + result + "\n\n", memberId, Long.valueOf(problemId));
    }


    @Async("sync-extension")
    public void fetchData(String memberId) {
        List<Long> problemIdList = Optional.ofNullable(codeRepository.getProblemIdByMemberId(memberId))
                .orElse(Collections.singletonList(0L));
        responseData.put(memberId, problemIdList);
        scheduler.schedule(() -> responseData.remove(memberId), 30, TimeUnit.SECONDS);
        log.info("비동기 처리 완료: 문제 ID 리스트 반환 -> {}", problemIdList);
    }

    public List<Long> getProblemIdList(String memberId) {
        return responseData.getOrDefault(memberId, Collections.emptyList());
    }

    @CachePut(value = "syncData", key = "#id")
    public Map<String, String> cacheSyncData(String id, Long problemId) {
        return Map.of("id", id, "problemId", String.valueOf(problemId));
    }

    @Cacheable(value = "syncData", key = "#id")
    public Map<String, String> getCachedData(String id) {
        return null;
    }

    public CommentListResponse allComment(Long codeNo) {
        CodeEntity code = codeRepository.findByIdWithComments(codeNo)
                .orElseThrow(() -> new RuntimeException("없는 코드입니다."));

        return CommentListResponse.fromRepoDto(code.getCommentList());
    }

    public CommentResponse insertComment(Long codeNo, String commentContent, String memberId) {
        CodeEntity code = checkedCode(codeNo);
        MemberEntity member = memberService.vaildMemberId(memberId);

        CommentEntity comment = CommentEntity.builder()
                .codeEntity(code)
                .memberEntity(member)
                .commentContent(commentContent)
                .commentDate(new Timestamp(System.currentTimeMillis()))
                .build();

        return new CommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(Long codeNo, Long commentNo, String memberId) {
        checkedCode(codeNo);
        CommentEntity comment = checkedComment(commentNo);

        if (!comment.getMemberEntity().getMemberId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    public CommentResponse updateComment(Long codeNo, Long commentNo, String commentContent, String memberId) {
        checkedCode(codeNo);
        CommentEntity comment = checkedComment(commentNo);

        if (!comment.getMemberEntity().getMemberId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.setCommentContent(commentContent);
        return new CommentResponse(commentRepository.save(comment));
    }

    public CommentEntity checkedComment(Long commentNo) {
        return commentRepository.findById(commentNo)
                .orElseThrow(() -> new RuntimeException("없는 댓글입니다."));
    }

    public CodeEntity checkedCode(Long codeNo) {
        return codeRepository.findById(codeNo)
                .orElseThrow(() -> new RuntimeException("없는 코드입니다."));
    }

    public List<CodeInfoDTO> getMemberSolvedInfo(String memberId, Long problemId) {
        List<CodeEntity> codeEntities = codeRepository.findByMemberEntity_MemberIdAndProblemEntity_ProblemId(memberId, problemId);
        List<CodeInfoDTO> codeInfoList = new ArrayList<>();

        for (CodeEntity codeEntity : codeEntities) {
            codeInfoList.add(CodeInfoDTO.fromEntity(codeEntity));
        }

        return codeInfoList;
    }

    public String memberSolvedEdit(String memberId, String problemId, String code) {
        int updatedRows = codeRepository.memberSolvedEdit(memberId, problemId, code);
        return (updatedRows > 0) ? "코드 수정 성공" : "코드 수정 실패";
    }

    public String memberSolvedDelete(String memberId, String problemId) {
        int updatedRows = codeRepository.memberSolvedDelete(memberId, problemId);
        return (updatedRows > 0) ? "코드 삭제 성공" : "코드 삭제 실패";
    }


}
