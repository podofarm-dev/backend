package com.mildo.dev.api.problem.repository;

import com.mildo.dev.api.problem.domain.dto.response.ProblemListDto;
import com.mildo.dev.api.problem.domain.dto.response.ProblemSolverDto;
import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query("SELECT new com.mildo.dev.api.problem.domain.dto.response.ProblemListDto(p.problemId, p.problemTitle, p.problemLevel, p.problemLink, c.codeStatus)" +
            "FROM ProblemEntity p left join CodeEntity c on p.problemId = c.problemId " +
            "and c.memberEntity.memberId = :memberId " +
            "ORDER BY p.problemNo ASC")
    List<ProblemListDto> findAllProblemList(@Param("memberId") String memberId, Pageable pageable);

    @Query("SELECT new com.mildo.dev.api.problem.domain.dto.response.ProblemListDto(p.problemId, p.problemTitle, p.problemLevel, p.problemLink, c.codeStatus) " +
            "FROM ProblemEntity p left join CodeEntity c on p.problemId = c.problemId  " +
            "and c.memberEntity.memberId = :memberId " +
            "WHERE p.problemTitle LIKE %:title% " +
            "ORDER BY p.problemNo ASC")
    List<ProblemListDto> findAllTitleProblemList(@Param("title") String title, @Param("memberId") String memberId, Pageable pageable);

    @Query("SELECT new com.mildo.dev.api.problem.domain.dto.response.ProblemSolverDto(c.problemId, m.imgUrl) " +
            "FROM CodeEntity c " +
            "JOIN MemberEntity m ON c.memberEntity.memberId = m.memberId " +
            "WHERE c.problemId IN :problemNos AND m.studyEntity.studyId = :studyId")
    List<ProblemSolverDto> findSolversByProblemNos(@Param("problemNos") List<Long> problemNos,
                                                   @Param("studyId") String studyId);


}