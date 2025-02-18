package com.mildo.dev.api.study.service;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.study.controller.dto.request.DailySolvedSearchCond;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyUpdateReqDto;
import com.mildo.dev.api.study.controller.dto.response.DailySolvedResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardFrameResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardGrassResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardSolvedCountResDto;
import com.mildo.dev.api.study.controller.dto.response.LogResDto;
import com.mildo.dev.api.study.controller.dto.response.StudyDetailResDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.domain.entity.StudyEntity;
import com.mildo.dev.api.study.repository.StudyRepository;
import com.mildo.dev.api.study.repository.dto.CountingSolvedDto;
import com.mildo.dev.api.study.repository.dto.GrassInfoDto;
import com.mildo.dev.api.study.repository.dto.ProblemInfoDto;
import com.mildo.dev.api.study.repository.dto.RecentActivityInfoDto;
import com.mildo.dev.api.study.repository.dto.StudyInfoDto;
import com.mildo.dev.api.utils.random.CodeGenerator;
import com.mildo.dev.global.exception.exceptionClass.AlreadyInStudyException;
import com.mildo.dev.global.exception.exceptionClass.NotInThatStudyException;
import com.mildo.dev.global.exception.exceptionClass.StudyPasswordMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.mildo.dev.global.exception.message.ExceptionMessage.ALREADY_IN_STUDY_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.MEMBER_NOT_FOUND_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.NOT_IN_THAT_STUDY_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.NOT_STUDY_LEADER_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.SOMEONE_NOT_IN_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.STUDY_NOT_FOUND_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.STUDY_PASSWORD_MISMATCH_MSG;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public StudySummaryResDto create(String memberId, StudyCreateReqDto requestDto) {
        //1. 스터디 생성
        LocalDate now = LocalDate.now();

        StudyEntity study = StudyEntity.builder()
                .studyId(CodeGenerator.generateRandomStudyCode()) //TODO 아이디 중복 여부 체크
                .studyName(requestDto.getName())
                .studyPwd(passwordEncoder.encode(requestDto.getPassword()))
                .studyStart(Date.valueOf(now))
                .studyEnd(Date.valueOf(now.plusMonths(1))) //TODO 우선 스터디 시작일로부터 한달 이후를 종료일로 자동 지정
                .build();

        //2. 스터디 저장
        StudyEntity savedStudy = studyRepository.save(study);

        //3. 사용자 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND_MSG));

        //4. 생성한 스터디에 리더로 가입
        joinStudyAsLeader(member, savedStudy);
        return StudySummaryResDto.from(savedStudy);
    }

    public void join(String memberId, StudyJoinReqDto requestDto) {
        //1. 해당 스터디가 존재하는지 확인
        StudyEntity study = studyRepository.findById(requestDto.getCode())
                .orElseThrow(() -> new NoSuchElementException(STUDY_NOT_FOUND_MSG));

        //2. 스터디의 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), study.getStudyPwd())) {
            throw new StudyPasswordMismatchException(STUDY_PASSWORD_MISMATCH_MSG);
        }

        //3. 사용자 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND_MSG));

        //4. 스터디 가입
        joinStudy(member, study);
    }

    @Transactional(readOnly = true)
    public DashBoardFrameResDto getDashBoardInfo(String memberId, String studyId) {
        //1. 사용자와 스터디의 존재 여부 및 관계 확인
        checkValidMemberAndStudy(memberId, studyId);

        //2. 스터디 및 사용자 정보 조회 -> repo 레이어의 dto 를 service/controller 레이어의 dto 로 변환
        StudyInfoDto repoDto = studyRepository.searchStudyWithMembers(studyId);
        return DashBoardFrameResDto.fromRepoDto(repoDto);
    }

    @Transactional(readOnly = true)
    public DashBoardGrassResDto getDashBoardGrass(String memberId, String studyId, YearMonth yearMonth) {
        //1. 사용자와 스터디의 존재 여부 및 관계 확인
        checkValidMemberAndStudy(memberId, studyId);

        //2. 스터디에 참여하고 있는 모든 사용자 ID 조회
        List<MemberEntity> members = memberRepository.findInStudySorted(studyId, memberId);
        List<String> memberIds = members.stream()
                .map(MemberEntity::getMemberId)
                .collect(toList());

        //3. 사용자별 yearMonth 에 해당하는 잔디 데이터 조회
        List<GrassInfoDto> repoDto = studyRepository.countSolvedPerDate(studyId, yearMonth);
        return DashBoardGrassResDto.fromRepoDto(memberIds, repoDto, yearMonth.lengthOfMonth());
    }

    @Transactional(readOnly = true)
    public DashBoardSolvedCountResDto getDashBoardSolvedCount(String memberId, String studyId, YearMonth yearMonth) {
        //1. 사용자와 스터디의 존재 여부 및 관계 확인
        checkValidMemberAndStudy(memberId, studyId);

        //2. 사용자별 해결한 문제 수 조회
        List<CountingSolvedDto> repoDto = studyRepository.countSolved(studyId, yearMonth);
        return DashBoardSolvedCountResDto.fromRepoDto(repoDto);
    }

    @Transactional(readOnly = true)
    public DailySolvedResDto getDailySolvedProblems(String memberId, String studyId, DailySolvedSearchCond cond) {
        //1. 사용자와 스터디의 존재 여부 및 관계 확인
        checkMembersInStudy(studyId, memberId, cond.getMember());

        //2. 해당 사용자가 해결한 문제 정보 조회
        List<ProblemInfoDto> repoDto = studyRepository.searchSolvedProblemInfo(cond.getDate(), cond.getMember());
        return DailySolvedResDto.fromRepoDto(repoDto);
    }

    @Transactional(readOnly = true)
    public LogResDto getLog(String memberId, String studyId) {
        //1. 사용자와 스터디의 존재 여부 및 관계 확인
        checkValidMemberAndStudy(memberId, studyId);

        //2. 해당 스터디의 최근 활동 정보 조회
        List<RecentActivityInfoDto> repoDto = studyRepository.searchRecentActivityInfo(studyId);
        return LogResDto.fromRepoDto(repoDto, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public StudyDetailResDto getStudyInfo(String memberId, String studyId) {
        //1. 스터디 정보와 회원 정보 조회
        StudyEntity study = getStudyWithMembers(memberId, studyId);

        return StudyDetailResDto.from(study);
    }

    public StudyDetailResDto updateStudy(String memberId, String studyId, StudyUpdateReqDto requestDto) {
        //1. 스터디 정보와 회원 정보 조회
        StudyEntity study = studyRepository.findByIdCascade(studyId)
                .orElseThrow(() -> new NoSuchElementException(STUDY_NOT_FOUND_MSG));

        //2. 스터디의 현재 리더가 memberId 인지 확인
        MemberEntity asIs = study.getLeader();
        if (!asIs.getMemberId().equals(memberId)) {
            throw new IllegalStateException(NOT_STUDY_LEADER_MSG);
        }

        //3. 스터디의 차기 리더가 해당 스터디에 속해있는지 확인
        MemberEntity toBe = study.getMember(requestDto.getLeaderId())
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND_MSG));

        //4. 스터디명 변경
        study.changeName(requestDto.getName());
        //5. 스터디장 변경
        study.changeLeader(asIs, toBe);

        return StudyDetailResDto.from(study);
    }

    private void joinStudyAsLeader(MemberEntity member, StudyEntity study) {
        checkIfJoined(member);

        member.setLeader("Y");
        member.setStudyEntity(study);
        member.setIsParticipant(study.getStudyStart());
    }

    private void joinStudy(MemberEntity member, StudyEntity study) {
        checkIfJoined(member);

        member.setStudyEntity(study);
        member.setIsParticipant(study.getStudyStart());
    }

    private void checkIfJoined(MemberEntity member) {
        if (member.getStudyEntity() != null) {
            throw new AlreadyInStudyException(ALREADY_IN_STUDY_MSG);
        }
    }

    private void checkValidMemberAndStudy(String memberId, String studyId) {
        //1. 사용자 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND_MSG));

        //2. memberId 사용자가 studyId 스터디에 속해있는지 확인
        if (member.getStudyEntity() == null
                || !member.getStudyEntity().getStudyId().equals(studyId)) {
            throw new NotInThatStudyException(NOT_IN_THAT_STUDY_MSG);
        }
    }

    private void checkMembersInStudy(String studyId, String... memberIds) {
        //1. 사용자 정보를 포함하여 스터디 조회
        StudyEntity study = studyRepository.findByIdCascade(studyId)
                .orElseThrow(() -> new NoSuchElementException(STUDY_NOT_FOUND_MSG));

        //2. 파라미터로 받은 memberIds 이 모두 스터디그룹에 속해있는지 확인
        if (!study.containsAll(List.of(memberIds))) {
            throw new NotInThatStudyException(SOMEONE_NOT_IN_MSG);
        }
    }

    private StudyEntity getStudyWithMembers(String memberId, String studyId) {
        StudyEntity study = studyRepository.findByIdCascade(studyId)
                .orElseThrow(() -> new NoSuchElementException(STUDY_NOT_FOUND_MSG));

        //2. memberId 회원이 study 에 속해있는지 확인
        if (!study.contains(memberId)) {
            throw new NotInThatStudyException(NOT_IN_THAT_STUDY_MSG);
        }

        return study;
    }
}
