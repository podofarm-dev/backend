package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.repository.dto.StudyInfoDto;

public interface CustomStudyRepository {

    StudyInfoDto searchStudyWithMembers(String studyId);

}
