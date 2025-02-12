package com.mildo.dev.api.study.service;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardFrameResDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.domain.entity.StudyEntity;
import com.mildo.dev.api.study.repository.StudyRepository;
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
import java.util.NoSuchElementException;

import static com.mildo.dev.global.exception.message.ExceptionMessage.ALREADY_IN_STUDY_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.MEMBER_NOT_FOUND_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.NOT_IN_THAT_STUDY_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.STUDY_NOT_FOUND_MSG;
import static com.mildo.dev.global.exception.message.ExceptionMessage.STUDY_PASSWORD_MISMATCH_MSG;

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
                .studyId(CodeGenerator.generateRandomCode()) //TODO 아이디 중복 여부 체크
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

    public DashBoardFrameResDto getDashBoardInfo(String memberId, String studyId) {
        //1. 사용자 조회
        log.info("사용자 조회");
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND_MSG));

        //2. memberId 사용자가 studyId 스터디에 속해있는지 확인
        log.info("사용자 확인");
        if (!member.getStudyEntity().getStudyId().equals(studyId)) {
            throw new NotInThatStudyException(NOT_IN_THAT_STUDY_MSG);
        }

        //3. 스터디 및 사용자 정보 조회 -> repo 레이어의 dto 를 service/controller 레이어의 dto 로 변환
        log.info("정보 조회");
        StudyInfoDto repoDto = studyRepository.searchStudyWithMembers(studyId);
        return DashBoardFrameResDto.fromRepoDto(repoDto);
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
}
