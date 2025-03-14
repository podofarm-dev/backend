package com.podofarm.dev.api.problem.repository;

import com.podofarm.dev.api.problem.domain.dto.request.ProblemSolverDto;
import com.podofarm.dev.api.problem.domain.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long>, ProblemRepositoryCustom {

    @Query("SELECT new com.podofarm.dev.api.problem.domain.dto.request.ProblemSolverDto(c.problemEntity.problemId, m.imgUrl, m.name) " +
            "FROM CodeEntity c " +
            "JOIN MemberEntity m ON c.memberEntity.memberId = m.memberId " +
            "WHERE c.problemEntity.problemId IN :problemNos AND m.studyEntity.studyId = :studyId")
    List<ProblemSolverDto> findSolversByProblemNos(@Param("problemNos") List<Long> problemNos,
                                                   @Param("studyId") String studyId);


    @Query("SELECT p.problemSolution FROM ProblemEntity p WHERE p.problemId = :problemId")
    String findSolutionByProblemId(@Param("problemId") Long problemId);}
