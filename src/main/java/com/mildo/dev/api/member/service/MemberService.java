package com.mildo.dev.api.member.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mildo.dev.api.code.domain.dto.request.CodeLevelDTO;
import com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import com.mildo.dev.api.code.domain.dto.response.SolvedListResponse;
import com.mildo.dev.api.code.domain.dto.response.SolvedProblemResponse;
import com.mildo.dev.api.code.repository.CodeRepository;
import com.mildo.dev.api.member.domain.dto.request.MemberReNameDto;
import com.mildo.dev.api.member.domain.dto.request.TokenDto;
import com.mildo.dev.api.member.domain.dto.response.MemberInfoDTO;
import com.mildo.dev.api.member.domain.dto.response.ProblemMemberDto;
import com.mildo.dev.api.member.domain.dto.response.SolvedMemberListDto;
import com.mildo.dev.api.member.domain.dto.response.TokenResponse;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.domain.entity.TokenEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.member.repository.TokenRepository;
import com.mildo.dev.global.exception.exceptionClass.TokenException;
import com.mildo.dev.global.oauth.jwt.JwtInterface;
import com.mildo.dev.global.oauth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtInterface jwtInterface;
    private final CodeRepository codeRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${BASIC_URL}")
    private String basic;

    private static final String REFRESH_SECRET_KEY = JwtTokenProvider.REFRESH_TOKEN_SECRET_KEY;

    public TokenDto token(String memberId){
        String accessToken = jwtInterface.getAccess(memberId);
        String refreshToken = jwtInterface.getRefresh(memberId);
        Timestamp refreshTime = jwtInterface.getRefreshExpiration(refreshToken);

        // 나중에 멤버 없으면 예외 처리;
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Optional<TokenEntity> existingToken = tokenRepository.findByMemberEntity_MemberId(memberId);
        TokenEntity token;
        if (existingToken.isPresent()) {
            token = existingToken.get(); // 객체 가져오기
            token.setRefreshToken(refreshToken);
            token.setAccessToken(accessToken);
            token.setRefreshExpirationTime(refreshTime);
        } else {
            token = TokenEntity.builder()
                    .memberEntity(memberEntity) // memberEntity 관련 만 들어갈 수 있음
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .refreshExpirationTime(refreshTime)
                    .build();
        }
        tokenRepository.save(token);
        return new TokenDto(memberId, accessToken, refreshToken);
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
                throw new TokenException("DB Warning - Login Again");
            } else{
                token = isToken.get();
                token.setAccessToken(accessToken);
            }
            tokenRepository.save(token);
            return new TokenResponse(claims.getSubject(), accessToken);
        } catch (ExpiredJwtException e) { // Token 만료 시 발생
            log.error("ExpiredJwtException e = {}", e.getMessage());
            throw new TokenException("expired - Login Again");
        }catch (Exception e) { // 유효하지 않으면
            log.error("Exception e = {}", e.getMessage());
            throw new TokenException("Faill - Login Again");
        }
    }

    public SolvedProblemResponse memberLevel(String memberId){
        MemberEntity member = vaildMemberId(memberId);
        List<CodeLevelDTO> CodeLeverCount = codeRepository.findSolvedProblemLevelCountByMemberId(memberId);
        return new SolvedProblemResponse(CodeLeverCount);
    }

    public SolvedListResponse solvedProblemList(String memberId, int page, int size, String title){
        MemberEntity member = vaildMemberId(memberId);
        Pageable pageable = PageRequest.of(page, size);

        List<CodeSolvedListDTO> results;
        if (title != null && !title.isEmpty()) {
            results = codeRepository.findSolvedProblemListTitleByMemberId(memberId, title, pageable);
        } else {
            results = codeRepository.findSolvedProblemListByMemberId(memberId, pageable);
        }

        return new SolvedListResponse(results);
    }

    public MemberEntity vaildMemberId(String memberId){
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다."));
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

    public MemberInfoDTO memberInfo(String memberId){
        return memberRepository.findByMemberId(memberId).map(member -> new MemberInfoDTO(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity().getStudyId(),
                member.getImgUrl()
        )).orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));
    }

    public MemberInfoDTO updateUser(MemberReNameDto nameDto, String memberId) {
        MemberEntity member = vaildMemberId(memberId);

        member.setName(nameDto.getName());
        memberRepository.save(member);
        return new MemberInfoDTO( member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity().getStudyId(),
                member.getImgUrl());
    }

    public MemberInfoDTO uploadImg(MultipartFile file, String memberId) throws IOException {
        MemberEntity member = vaildMemberId(memberId);

        if(!basic.equals(member.getImgUrl())){
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, memberId));
        }

        String fileName = "profile/" + memberId;
        String fileUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bucket + "/" + fileName;

        ObjectMetadata metadata= new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3Client.putObject(bucket,fileName,file.getInputStream(),metadata);

        member.setImgUrl(fileUrl);
        memberRepository.save(member);

        return new MemberInfoDTO( member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getStudyEntity().getStudyId(),
                member.getImgUrl());
    }

    public void deleteMember(String memberId){
        MemberEntity member = vaildMemberId(memberId);

        long count = memberRepository.countMembersByStudyId(member.getStudyEntity().getStudyId());

        if("Y".equals(member.getLeader()) && count > 1){
            throw new RuntimeException("YOU LEADER CHANGE");
        }

        if(!basic.equals(member.getImgUrl())){
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, memberId));
        }

        memberRepository.delete(member);

//        if (count == 1) {
//            studyRepository.deleteById(member.getStudyEntity().getStudyId());
//        }
    }

    public ProblemMemberDto problemMember(String memberId){
        MemberEntity member = vaildMemberId(memberId);
        return memberRepository.countProblemByMemberId(memberId);
    }

    public Optional<SolvedMemberListDto> solvedMember(String memberId, String studyId){
        MemberEntity member = vaildMemberId(memberId);

        List<SolvedMemberListDto> members = memberRepository.solvedMemberRanking(studyId);
        // sort 메서드를 이용해서 Integer.compare 정수 비교 메서드 사용해서 정렬
        // a 객체가 0번째 인덱스에 오고 b 객체가 a보다 크면 앞으로 오고 작으면 뒤로 가고 값이 같으면 순서를 바꾸지 않는 방식
        members.sort((a, b) -> Integer.compare(b.getSolvedProblem(), a.getSolvedProblem()));

        int rank = 1;
        for (int i = 0; i < members.size(); i++) {
            if (i > 0 && members.get(i).getSolvedProblem() < members.get(i - 1).getSolvedProblem()) {
                rank = i + 1;
            }
            members.get(i).setRank(rank);
        }

        return members.stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst();
    }

}
