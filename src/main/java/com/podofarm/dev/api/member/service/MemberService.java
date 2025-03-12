package com.podofarm.dev.api.member.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.podofarm.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import com.podofarm.dev.api.code.repository.CodeRepository;
import com.podofarm.dev.api.member.domain.dto.request.MemberReNameDto;
import com.podofarm.dev.api.member.domain.dto.request.TokenDto;
import com.podofarm.dev.api.member.domain.dto.response.*;
import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import com.podofarm.dev.api.member.domain.entity.TokenEntity;
import com.podofarm.dev.api.member.repository.MemberRepository;
import com.podofarm.dev.api.member.repository.TokenRepository;
import com.podofarm.dev.api.study.service.StudyService;
import com.podofarm.dev.global.exception.exceptionClass.MemberEqException;
import com.podofarm.dev.global.exception.exceptionClass.TokenException;
import com.podofarm.dev.global.oauth.jwt.JwtInterface;
import com.podofarm.dev.global.oauth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    public static final String LOGGED_IN_MEMEBER_DIFFERENT = "memberId와 로그인한 사용자의 ID가 다릅니다.";
    public static final String MEMBER_NOT_FOUND = "해당 ID의 회원이 존재하지 않습니다.";
    public static final String LEADER_CHANGE_REQUEST = "리더 번경 후 재 실행 바랍니다.";
    public static final String LOGIN_AGAIN = "재 로그인 요청 바랍니다.";
    public static final String NO_FILES_TO_UPLOAD = "업로드할 파일이 없습니다.";

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final StudyService studyService;
    private final JwtInterface jwtInterface;
    private final CodeRepository codeRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${BASIC_URL}")
    private String basic;

    private static final String REFRESH_SECRET_KEY = JwtTokenProvider.REFRESH_TOKEN_SECRET_KEY;

    public TokenDto generateToken(String memberId){
        MemberEntity member = vaildMemberId(memberId);

        String accessToken = jwtInterface.getAccess(memberId);
        String refreshToken = jwtInterface.getRefresh(memberId);
        Timestamp refreshTime = jwtInterface.getRefreshExpiration(refreshToken);

        TokenEntity token = saveOrUpdateToken(member, accessToken, refreshToken, refreshTime);

        return new TokenDto(memberId, accessToken, refreshToken);
    }

    private TokenEntity saveOrUpdateToken(MemberEntity member, String accessToken, String refreshToken, Timestamp refreshTime) {
        return tokenRepository.findByMemberEntity_MemberId(member.getMemberId())
                .map(existingToken -> updateToken(existingToken, accessToken, refreshToken, refreshTime))
                .orElseGet(() -> createNewToken(member, accessToken, refreshToken, refreshTime));
        //  orElseGet() Optional에 값이 없을 때 실행
    }

    private TokenEntity updateToken(TokenEntity token, String accessToken, String refreshToken, Timestamp refreshTime) {
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setRefreshExpirationTime(refreshTime);
        return tokenRepository.save(token);
    }

    private TokenEntity createNewToken(MemberEntity member, String accessToken, String refreshToken, Timestamp refreshTime) {
        TokenEntity token = TokenEntity.builder()
                .memberEntity(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshExpirationTime(refreshTime)
                .build();
        return tokenRepository.save(token);
    }

    public TokenResponse refreshNew(String RefreshToken){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .parseClaimsJws(RefreshToken)
                    .getBody();

            Optional<TokenEntity> isToken = tokenRepository.findByRefreshToken(RefreshToken); // DB에 토큰 조회
            String accessToken = jwtInterface.getAccess(claims.getSubject()); // 새로운 토큰 생성

            TokenEntity token;
            if(!isToken.isPresent()){
                throw new TokenException(LOGIN_AGAIN);
            } else{
                token = isToken.get();
                token.setAccessToken(accessToken);
            }
            tokenRepository.save(token);
            return new TokenResponse(claims.getSubject(), accessToken);
        } catch (ExpiredJwtException e) { // Token 만료 시 발생
            log.error("ExpiredJwtException e = {}", e.getMessage());
            throw new TokenException(LOGIN_AGAIN);
        }catch (Exception e) { // 유효하지 않으면
            log.error("Exception e = {}", e.getMessage());
            throw new TokenException(LOGIN_AGAIN);
        }
    }

    public SolvedListResponse solvedProblemList(String memberId, int page, int size, String title){
        MemberEntity member = vaildMemberId(memberId);
        Pageable pageable = PageRequest.of(page, size);

        Page<CodeSolvedListDTO> results = title != null && !title.isEmpty()
                ? codeRepository.findSolvedProblemListTitleByMemberId(memberId, title, pageable)
                : codeRepository.findSolvedProblemListByMemberId(memberId, pageable);

        return SolvedListResponse.solvedDto(results);
    }

    public void tokenDelete(String memberId){
        Optional<TokenEntity> tokenEntity = tokenRepository.findByMemberEntity_MemberId(memberId);

        TokenEntity token;
        if (tokenEntity.isPresent()) {
            token = tokenEntity.get();
            token.setRefreshToken(null);
            token.setAccessToken(null);
            token.setRefreshExpirationTime(null);
            tokenRepository.save(token);
        }
    }

    public MemberInfoResponse memberInfo(String memberId){
        return memberRepository.findByMemberId(memberId).map(member -> new MemberInfoResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity() != null ? member.getStudyEntity().getStudyId() : null,
                member.getImgUrl()
        )).orElseThrow(() -> new RuntimeException(MEMBER_NOT_FOUND));
    }

    public MemberInfoResponse updateUser(MemberReNameDto nameDto, String memberId) {
        MemberEntity member = vaildMemberId(memberId);

        member.setName(nameDto.getName());
        memberRepository.save(member);
        return new MemberInfoResponse( member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity().getStudyId(),
                member.getImgUrl());
    }

    public MemberInfoResponse uploadImg(MultipartFile file, String reqMemberId, String memberId) throws IOException {
        isSameMember(reqMemberId, memberId);
        MemberEntity member = vaildMemberId(memberId);

        if (file.isEmpty()) {
            throw new IllegalArgumentException(NO_FILES_TO_UPLOAD);
        }

        for(int i = 1; i <= 6; i++){
            String basicWithNumber = basic + i;
            if(basicWithNumber.equals(member.getImgUrl())){
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, memberId));
                break;
            }
        }

        String fileUrl = uploadToS3(file, memberId);

        member.setImgUrl(fileUrl);
        memberRepository.save(member);

        return new MemberInfoResponse( member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity().getStudyId(),
                member.getImgUrl());
    }

    private String uploadToS3(MultipartFile file, String memberId) throws IOException {
        String fileName = "profile/" + memberId;
        String fileUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bucket + "/" + fileName;

        ObjectMetadata metadata= new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket,fileName, file.getInputStream(),metadata);
        return fileUrl;
    }

    @Transactional
    public void deleteMember(String reqMemberId, String memberId){
        isSameMember(reqMemberId, memberId);
        MemberEntity member = vaildMemberId(memberId);

        if (member.getStudyEntity() == null || member.getStudyEntity().getStudyId() == null) {
            deleteMemberAndS3(member);
            return;
        }

        long count = memberRepository.countMembersByStudyId(member.getStudyEntity().getStudyId());

        if("Y".equals(member.getLeader()) && count > 1){
            throw new RuntimeException(LEADER_CHANGE_REQUEST);
        }

        if (count == 1) {
            studyService.remove(member.getMemberId(), member.getStudyEntity().getStudyId());
        }

        deleteMemberAndS3(member);
    }

    private void deleteMemberAndS3(MemberEntity member) {
        try {
            memberRepository.delete(member);
        } catch (Exception e) {
            throw new RuntimeException("멤버 삭제 중 오류가 발생 DB 롤백", e);
        }

        if (!basic.equals(member.getImgUrl())) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, member.getMemberId()));
        }
    }

    public ProblemPageInfoResponse problemPageInfo(String reqMemberId, String memberId){
        isSameMember(reqMemberId, memberId);
        MemberEntity member = vaildMemberId(memberId);

        return memberRepository.countProblemByMemberId(memberId);
    }

    public Optional<SolvedMemberListResponse> solvedMember(String memberId, String studyId){
        MemberEntity member = vaildMemberId(memberId);

        List<SolvedMemberListResponse> members = memberRepository.solvedMemberRanking(studyId);

        assignRanks(members);

        return members.stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst();
    }

    private void assignRanks(List<SolvedMemberListResponse> members) {
        // 문제 풀이 수를 기준으로 내림차순 정렬
        // sort 메서드를 이용해서 Integer.compare 정수 비교 메서드 사용해서 정렬
        // a 객체가 0번째 인덱스에 오고 b 객체가 a보다 크면 앞으로 오고 작으면 뒤로 가고 값이 같으면 순서를 바꾸지 않는 방식
        members.sort((a, b) -> Integer.compare(b.getSolvedProblem(), a.getSolvedProblem()));

        // 순위 부여
        int rank = 1;
        for (int i = 0; i < members.size(); i++) {
            if (i > 0 && members.get(i).getSolvedProblem() < members.get(i - 1).getSolvedProblem()) {
                rank = i + 1;
            }
            members.get(i).setRank(rank);
        }
    }

    public MemberEntity vaildMemberId(String memberId){
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND));
    }

    private static void isSameMember(String reqMemberId, String memberId) {
        if (!memberId.equals(reqMemberId)) {
            throw new MemberEqException(LOGGED_IN_MEMEBER_DIFFERENT);
        }
    }

    public boolean checkExtensionSync(String userId, String studyId) {
        return memberRepository.checkExtensionSync(userId, studyId);
    }
}
