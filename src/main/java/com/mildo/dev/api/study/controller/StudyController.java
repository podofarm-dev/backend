package com.mildo.dev.api.study.controller;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.mildo.dev.api.study.controller.dto.response.MessageResDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
@Slf4j
public class StudyController {

    private static final String STUDY_JOIN_SUCCEED = "스터디 참여 완료";

    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<StudySummaryResDto> create(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody @Valid StudyCreateReqDto requestDto
    ) {
        StudySummaryResDto responseDto = studyService.create(customUser.getMemberId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/enter-study")
    public ResponseEntity<MessageResDto> join(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody @Valid StudyJoinReqDto requestDto
    ) {
        studyService.join(customUser.getMemberId(), requestDto);
        return ResponseEntity.ok(MessageResDto.success(STUDY_JOIN_SUCCEED));
    }

}
