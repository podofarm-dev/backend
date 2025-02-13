package com.mildo.dev.api.study.controller;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardFrameResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardGrassResDto;
import com.mildo.dev.api.study.controller.dto.response.MessageResDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

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

    @GetMapping("/{studyId}/member-list")
    public ResponseEntity<DashBoardFrameResDto> dashBoardFrame(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId
    ) {
        DashBoardFrameResDto responseDto = studyService.getDashBoardInfo(customUser.getMemberId(), studyId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{studyId}/grass")
    public ResponseEntity<DashBoardGrassResDto> dashBoardGrass(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            @RequestParam(value = "yearMonth", required = false) YearMonth param
    ) {
        YearMonth yearMonth = (param != null) ? param : YearMonth.now();
        DashBoardGrassResDto responseDto = studyService.getDashBoardGrass(customUser.getMemberId(), studyId, yearMonth);
        return ResponseEntity.ok(responseDto);
    }

}
