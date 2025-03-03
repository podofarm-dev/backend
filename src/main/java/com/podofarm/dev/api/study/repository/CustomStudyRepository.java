package com.podofarm.dev.api.study.repository;

import com.podofarm.dev.api.study.repository.dto.CountingSolvedDto;
import com.podofarm.dev.api.study.repository.dto.GrassInfoDto;
import com.podofarm.dev.api.study.repository.dto.ProblemInfoDto;
import com.podofarm.dev.api.study.repository.dto.RecentActivityInfoDto;
import com.podofarm.dev.api.study.repository.dto.StudyInfoDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface CustomStudyRepository {

    StudyInfoDto searchStudyWithMembers(String studyId);

    List<GrassInfoDto> countSolvedPerDate(String studyId, YearMonth yearMonth);

    List<CountingSolvedDto> countSolved(String studyId, YearMonth yearMonth);

    List<ProblemInfoDto> searchSolvedProblemInfo(LocalDate date, String memberId);

    List<RecentActivityInfoDto> searchTodayActivityInfo(String studyId);
}

