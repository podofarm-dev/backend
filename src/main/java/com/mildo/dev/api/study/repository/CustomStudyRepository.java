package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.repository.dto.CountingSolvedDto;
import com.mildo.dev.api.study.repository.dto.GrassInfoDto;
import com.mildo.dev.api.study.repository.dto.StudyInfoDto;

import java.time.YearMonth;
import java.util.List;

public interface CustomStudyRepository {

    StudyInfoDto searchStudyWithMembers(String studyId);

    List<GrassInfoDto> countSolvedPerDate(String studyId, YearMonth yearMonth);

    List<CountingSolvedDto> countSolved(String studyId, YearMonth yearMonth);
}
