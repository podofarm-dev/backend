package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.domain.entity.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<StudyEntity, String>, CustomStudyRepository {
}
