package com.mildo.dev.api.study.service;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.domain.entity.StudyEntity;
import com.mildo.dev.api.study.repository.StudyRepository;
import com.mildo.dev.api.utils.random.CodeGenerator;
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

import static com.mildo.dev.global.exception.message.ExceptionMessage.MEMBER_NOT_FOUND_MSG;
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
    private StudyCreateReqDto requestDto;

    @BeforeEach
    void setUp() {
        member = MemberEntity.builder()
                .memberId("ABC123")
                .name("testmember")
                .googleId("123456")
                .email("test@gmail.com")
                .leader("N")
                .build();

        requestDto = StudyCreateReqDto.builder()
                .name("teststudy")
                .password("pass1234")
                .build();
    }

    @Nested
    @DisplayName("스터디 생성 로직 테스트")
    class createStudyTest {

        @Test
        @DisplayName("성공")
        void success() {
            // given
            String encodedPassword = "encodedPassword";
            String generatedStudyId = "DEF456";
            LocalDate now = LocalDate.now();

            when(passwordEncoder.encode(requestDto.getPassword()))
                    .thenReturn(encodedPassword);
            when(studyRepository.save(any(StudyEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(memberRepository.findByMemberId(member.getMemberId()))
                    .thenReturn(Optional.of(member));
            mockStatic(CodeGenerator.class).when(CodeGenerator::generateRandomCode)
                    .thenReturn(generatedStudyId);

            // When
            StudySummaryResDto result = studyService.create(member.getMemberId(), requestDto);

            // Then
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
            verify(memberRepository, times(1)).findByMemberId(member.getMemberId());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void memberNotFound() {
            // Given
            when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(()
                    -> studyService.create("InvalidMemberId", requestDto)
            )
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage(MEMBER_NOT_FOUND_MSG);
        }
    }

}