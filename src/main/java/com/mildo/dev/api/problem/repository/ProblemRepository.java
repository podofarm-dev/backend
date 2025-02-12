package com.mildo.dev.api.problem.repository;

import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query(value = "SELECT p.problem_no, p.problem_title, p.problem_level, p.problem_link, p.problem_type, p.problem_id " +
            "FROM problem p ORDER BY p.problem_no ASC", nativeQuery = true)
    List<Object[]> findAllProblemList();

}