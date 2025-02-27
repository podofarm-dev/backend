package com.podofarm.dev.api.study.service;

import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import com.podofarm.dev.api.member.repository.MemberRepository;
import com.podofarm.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.podofarm.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.podofarm.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.podofarm.dev.api.study.domain.entity.StudyEntity;
import com.podofarm.dev.api.study.repository.StudyRepository;
import com.podofarm.dev.api.utils.random.CodeGenerator;
import com.podofarm.dev.global.exception.exceptionClass.AlreadyInStudyException;
import com.podofarm.dev.global.exception.exceptionClass.StudyPasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.podofarm.dev.global.exception.message.ExceptionMessage.ALREADY_IN_STUDY_MSG;
import static com.podofarm.dev.global.exception.message.ExceptionMessage.MEMBER_NOT_FOUND_MSG;
import static com.podofarm.dev.global.exception.message.ExceptionMessage.STUDY_NOT_FOUND_MSG;
import static com.podofarm.dev.global.exception.message.ExceptionMessage.STUDY_PASSWORD_MISMATCH_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudyService studyService;

    private MemberEntity member;

    @BeforeEach
    void setUp() {
        member = MemberEntity.builder()
                .memberId("ABC123")
                .name("testmember")
                .googleId("123456")
                .email("test@gmail.com")
                .leader("N")
                .build();
    }

    @Nested
    @DisplayName("스터디 생성 로직 테스트")
    class createStudyTest {

        private StudyCreateReqDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = StudyCreateReqDto.builder()
                    .name("teststudy")
                    .password("pass1234")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            String encodedPassword = "encodedPassword";
            String generatedStudyId = "DEF45";
            LocalDate now = LocalDate.now();

            when(passwordEncoder.encode(requestDto.getPassword()))
                    .thenReturn(encodedPassword);
            when(studyRepository.save(any(StudyEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(memberRepository.findById(member.getMemberId()))
                    .thenReturn(Optional.of(member));
            mockStatic(CodeGenerator.class).when(CodeGenerator::generateRandomStudyCode)
                    .thenReturn(generatedStudyId);

            // when
            StudySummaryResDto result = studyService.create(member.getMemberId(), requestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(generatedStudyId);
            assertThat(result.getName()).isEqualTo("teststudy");
            assertThat(result.getStartDate()).isEqualTo(now.toString());
            assertThat(result.getEndDate()).isEqualTo(now.plusMonths(1).toString());

            assertThat(member.getLeader()).isEqualTo("Y");
            assertThat(member.getStudyEntity())
                    .usingRecursiveComparison()
                    .isEqualTo(StudyEntity.builder()
                            .studyId(generatedStudyId)
                            .studyName("teststudy")
                            .studyPwd(encodedPassword)
                            .studyStart(Date.valueOf(now))
                            .studyEnd(Date.valueOf(now.plusMonths(1)))
                            .build()
                    );
            assertThat(member.getIsParticipant()).isEqualTo(Date.valueOf(now));

            verify(studyRepository, times(1)).save(any(StudyEntity.class));
            verify(memberRepository, times(1)).findById(member.getMemberId());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void memberNotFound() {
            // given
            when(memberRepository.findById(anyString()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(()
                    -> studyService.create("InvalidMemberId", requestDto))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage(MEMBER_NOT_FOUND_MSG);
        }
    }

    @Nested
    @DisplayName("스터디 참여 로직 테스트")
    class joinStudyTest {

        private StudyJoinReqDto requestDto;

        public static final String STUDY_ID = "ABC12";
        public static final String STUDY_NAME = "studyA";
        public static final String STUDY_PASSWORD = "password";
        public static final String ENCODED_PASSWORD = "encodedPassword";

        @BeforeEach
        void setUp() {
            requestDto = StudyJoinReqDto.builder()
                    .code(STUDY_ID)
                    .password(STUDY_PASSWORD)
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            LocalDate now = LocalDate.now();

            StudyEntity study = StudyEntity.builder()
                    .studyId(STUDY_ID)
                    .studyName(STUDY_NAME)
                    .studyPwd(ENCODED_PASSWORD)
                    .studyStart(Date.valueOf(now))
                    .studyEnd(Date.valueOf(now.plusMonths(1)))
                    .build();

            when(passwordEncoder.matches(STUDY_PASSWORD, ENCODED_PASSWORD))
                    .thenReturn(true);
            when(studyRepository.findById(study.getStudyId()))
                    .thenReturn(Optional.of(study));
            when(memberRepository.findById(member.getMemberId()))
                    .thenReturn(Optional.of(member));

            // when
            studyService.join(member.getMemberId(), requestDto);

            // then
            assertThat(member.getLeader()).isEqualTo("N");
            assertThat(member.getStudyEntity())
                    .usingRecursiveComparison()
                    .isEqualTo(StudyEntity.builder()
                            .studyId(STUDY_ID)
                            .studyName(STUDY_NAME)
                            .studyPwd(ENCODED_PASSWORD)
                            .studyStart(Date.valueOf(now))
                            .studyEnd(Date.valueOf(now.plusMonths(1)))
                            .build()
                    );
            assertThat(member.getIsParticipant()).isEqualTo(Date.valueOf(now));

            verify(studyRepository, times(1)).findById(STUDY_ID);
            verify(memberRepository, times(1)).findById(member.getMemberId());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 스터디")
        void studyNotFound() {
            // given
            when(studyRepository.findById(anyString()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(()
                    -> studyService.join(member.getMemberId(), requestDto))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage(STUDY_NOT_FOUND_MSG);
        }

        @Test
        @DisplayName("실패 - 스터디 비밀번호 불일치")
        void studyPasswordMismatch() {
            // given
            LocalDate now = LocalDate.now();
            StudyEntity study = StudyEntity.builder()
                    .studyId(STUDY_ID)
                    .studyName(STUDY_NAME)
                    .studyPwd(ENCODED_PASSWORD)
                    .studyStart(Date.valueOf(now))
                    .studyEnd(Date.valueOf(now.plusMonths(1)))
                    .build();

            when(studyRepository.findById(study.getStudyId()))
                    .thenReturn(Optional.of(study));
            when(passwordEncoder.matches(STUDY_PASSWORD, ENCODED_PASSWORD))
                    .thenReturn(false);

            // when & then
            assertThatThrownBy(()
                    -> studyService.join(member.getMemberId(), requestDto))
                    .isInstanceOf(StudyPasswordMismatchException.class)
                    .hasMessage(STUDY_PASSWORD_MISMATCH_MSG);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void memberNotFound() {
            // given
            LocalDate now = LocalDate.now();
            StudyEntity study = StudyEntity.builder()
                    .studyId(STUDY_ID)
                    .studyName(STUDY_NAME)
                    .studyPwd(ENCODED_PASSWORD)
                    .studyStart(Date.valueOf(now))
                    .studyEnd(Date.valueOf(now.plusMonths(1)))
                    .build();

            when(studyRepository.findById(study.getStudyId()))
                    .thenReturn(Optional.of(study));
            when(passwordEncoder.matches(STUDY_PASSWORD, ENCODED_PASSWORD))
                    .thenReturn(true);
            when(memberRepository.findById(anyString()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(()
                    -> studyService.join("InvalidMemberId", requestDto))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage(MEMBER_NOT_FOUND_MSG);
        }

        @Test
        @DisplayName("실패 - 이미 가입한 스터디가 있는 사용자")
        void alreadyInStudy() {
            // given
            LocalDate now = LocalDate.now();
            StudyEntity study = StudyEntity.builder()
                    .studyId(STUDY_ID)
                    .studyName(STUDY_NAME)
                    .studyPwd(ENCODED_PASSWORD)
                    .studyStart(Date.valueOf(now))
                    .studyEnd(Date.valueOf(now.plusMonths(1)))
                    .build();

            member.setStudyEntity(study);

            when(passwordEncoder.matches(STUDY_PASSWORD, ENCODED_PASSWORD))
                    .thenReturn(true);
            when(studyRepository.findById(study.getStudyId()))
                    .thenReturn(Optional.of(study));
            when(memberRepository.findById(member.getMemberId()))
                    .thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(()
                    -> studyService.join(member.getMemberId(), requestDto))
                    .isInstanceOf(AlreadyInStudyException.class)
                    .hasMessage(ALREADY_IN_STUDY_MSG);
        }

    }
}