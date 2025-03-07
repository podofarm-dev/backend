package com.podofarm.dev.api.code.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.podofarm.dev.api.code.domain.dto.CodeInfoDTO;
import com.podofarm.dev.api.code.domain.dto.UploadDTO;
import com.podofarm.dev.api.code.domain.dto.request.OpenAIRequest;
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
import com.podofarm.dev.api.problem.domain.entity.ProblemEntity;
import com.podofarm.dev.api.problem.repository.ProblemRepository;
import com.podofarm.dev.global.OpenAI.OpenAIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final OpenAIConfig openAiConfig;
    private final WebClient webClient;

    // TTL 적용, 전역변수 설정으로 메모리 관리
    private final Map<String, List<Long>> responseData = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void upload(JsonNode request) {
        long startTime = System.currentTimeMillis(); // ⏱️ 전체 실행 시작 시간
        log.info("[UPLOAD PROCESS] 시작...");

        UploadDTO uploadDTO = new UploadDTO(request);
        long dtoParseTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] DTO 변환 완료, 소요 시간: {} ms", (dtoParseTime - startTime));

        // 1. OpenAI 코드 분석 API 호출
        long openAIStartTime = System.currentTimeMillis();
        OpenAIRequest openAIRequest = new OpenAIRequest();
        openAIRequest.setCode(uploadDTO.getSource());

        OpenAIResponse aiResponse = analyzeCode(openAIRequest);
        String analyzedSource = aiResponse.getAnalyzedCode();
        long openAIEndTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] OpenAI 코드 분석 완료, 소요 시간: {} ms", (openAIEndTime - openAIStartTime));

        // 2. problemId로 problem 테이블에서 problemSolution 조회 후 source에 추가
        long dbQueryStartTime = System.currentTimeMillis();
        ProblemEntity problemEntity = problemRepository.findById(Long.parseLong(uploadDTO.getProblemId()))
                .orElseThrow(() -> new IllegalArgumentException("해당 문제 ID가 존재하지 않습니다: " + uploadDTO.getProblemId()));
        long dbQueryEndTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] 문제 테이블 조회 완료, 소요 시간: {} ms", (dbQueryEndTime - dbQueryStartTime));

        // 3. 최종 코드 생성
        String finalSource = problemEntity.getProblemSolution() + analyzedSource;
        long finalSourceTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] 최종 코드 생성 완료, 소요 시간: {} ms", (finalSourceTime - dbQueryEndTime));

        // 4. 회원 조회
        long memberQueryStartTime = System.currentTimeMillis();
        MemberEntity memberEntity = memberRepository.findById(uploadDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다: " + uploadDTO.getMemberId()));
        long memberQueryEndTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] 회원 조회 완료, 소요 시간: {} ms", (memberQueryEndTime - memberQueryStartTime));

        // 5. 코드 저장
        long saveStartTime = System.currentTimeMillis();
        CodeEntity codeEntity = CodeEntity.builder()
                .memberEntity(memberEntity)
                .problemEntity(problemEntity)
                .codeSource(finalSource)
                .codeSolvedDate(uploadDTO.getSolvedDateAsTimestamp())
                .codeTime(Time.valueOf(uploadDTO.getTime()))
                .codeStatus(uploadDTO.isStatus())
                .codePerformance(uploadDTO.getPerformance())
                .codeAccuracy(uploadDTO.getAccuracy())
                .build();
        codeRepository.save(codeEntity);
        long saveEndTime = System.currentTimeMillis();
        log.info("[UPLOAD PROCESS] 코드 저장 완료, 소요 시간: {} ms", (saveEndTime - saveStartTime));

        long totalExecutionTime = saveEndTime - startTime;
        log.info("✅ [UPLOAD PROCESS] 전체 실행 시간: {} ms", totalExecutionTime);
    }


    @CachePut(value = "syncData", key = "#id")
    public Map<String, String> cacheSyncData(String id, Long problemId) {
        Map<String, String> cachedData = Map.of(
                "id", id,
                "problemId", String.valueOf(problemId)
        );

        System.out.println("✅ 캐시에 저장됨: " + cachedData);
        return cachedData;
    }

    @Cacheable(value = "syncData", key = "#id")
    public Map<String, String> getCachedData(String id) {
        System.out.println("❌ 캐시에 데이터 없음 (DB 조회 필요) - ID: " + id);
        return null; // 캐시에 데이터가 없으면 null 반환
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

    public OpenAIResponse analyzeCode(OpenAIRequest request) {
        String prompt = "다음 Java 코드를 분석하고, 적절한 주석을 `/** ... */` 형식으로 코드 상단에 추가해 주세요.\n" +
                "반환 형식 예시:\n" +
                "/***OPEN AI***\n" +
                " *  1. 푼 문제를 다시 봤을 때 흐름을 알게끔 하기 위함" +
                " *  2. 어떤 메소드나 함수를 썼는지 차례로 정리" +
                " *  " +
                "******/\n" +
                "코드:\n" + request.getCode() ;

        Map<String, Object> requestBody = Map.of(
                "model", openAiConfig.getModel(),
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.3
        );

        OpenAIResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();

        return response;
    }

    @Async("sync-extension")
    public CompletableFuture<List<Long>> fetchData(String memberId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Long> problemIdList = codeRepository.getProblemIdByMemberId(memberId);
            responseData.put(memberId, problemIdList);
            scheduler.schedule(() -> responseData.remove(memberId), 30, TimeUnit.SECONDS); // 30초 후 삭제

            log.info("비동기 처리 완료: 문제 ID 리스트 반환 -> " + problemIdList);
            return problemIdList;
        });
    }

    public List<Long> getProblemIdList(String memberId) {
        return responseData.getOrDefault(memberId, Collections.emptyList());
    }

}
