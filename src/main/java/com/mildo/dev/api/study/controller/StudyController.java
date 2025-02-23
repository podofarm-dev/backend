package com.mildo.dev.api.study.controller;

import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.study.controller.dto.request.DailySolvedSearchCond;
import com.mildo.dev.api.study.controller.dto.request.StudyCreateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyJoinReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyLeaderUpdateReqDto;
import com.mildo.dev.api.study.controller.dto.request.StudyNameUpdateReqDto;
import com.mildo.dev.api.study.controller.dto.response.DailySolvedResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardFrameResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardGrassResDto;
import com.mildo.dev.api.study.controller.dto.response.DashBoardSolvedCountResDto;
import com.mildo.dev.api.study.controller.dto.response.LogResDto;
import com.mildo.dev.api.study.controller.dto.response.MessageResDto;
import com.mildo.dev.api.study.controller.dto.response.StudyDetailResDto;
import com.mildo.dev.api.study.controller.dto.response.StudySummaryResDto;
import com.mildo.dev.api.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/{studyId}/solved-problems")
    public ResponseEntity<DashBoardSolvedCountResDto> dashBoardSolvedCount(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            @RequestParam(value = "yearMonth", required = false) YearMonth yearMonth
    ) {
        DashBoardSolvedCountResDto responseDto = studyService.getDashBoardSolvedCount(customUser.getMemberId(), studyId, yearMonth);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{studyId}/daily-solved")
    public ResponseEntity<DailySolvedResDto> dailSolved(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            DailySolvedSearchCond requestParam
    ) {
        DailySolvedResDto responseDto = studyService.getDailySolvedProblems(customUser.getMemberId(), studyId, requestParam);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{studyId}/logs")
    public ResponseEntity<LogResDto> logs(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId
    ) {
        LogResDto responseDto = studyService.getLog(customUser.getMemberId(), studyId);
        return ResponseEntity.ok(responseDto);
    }

    //TODO 스터디장만 해당 API를 호출할 수 있도록 막아놓을지
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyDetailResDto> studyInfo(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId
    ) {
        StudyDetailResDto responseDto = studyService.getStudyInfo(customUser.getMemberId(), studyId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{studyId}/name")
    public ResponseEntity<StudyDetailResDto> modifyStudyName(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            @RequestBody StudyNameUpdateReqDto requestDto
    ) {
        StudyDetailResDto responseDto = studyService.updateStudyName(customUser.getMemberId(), studyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{studyId}/leader")
    public ResponseEntity<StudyDetailResDto> modifyStudyLeader(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            @RequestBody StudyLeaderUpdateReqDto requestDto
    ) {
        StudyDetailResDto responseDto = studyService.updateStudyLeader(customUser.getMemberId(), studyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{studyId}/members/me")
    public ResponseEntity<Void> leaveStudy(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId
    ) {
        studyService.leave(customUser.getMemberId(), studyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studyId}/members/{memberId}")
    public ResponseEntity<Void> dismissStudyMember(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId,
            @PathVariable String memberId
    ) {
        studyService.dismiss(customUser.getMemberId(), studyId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> removeStudy(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable String studyId
    ) {
        studyService.remove(customUser.getMemberId(), studyId);
        return ResponseEntity.ok().build();
    }


}
