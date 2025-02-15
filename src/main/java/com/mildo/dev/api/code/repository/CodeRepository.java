package com.mildo.dev.api.code.repository;

import com.mildo.dev.api.code.domain.dto.request.CodeLevelDTO;
import com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import com.mildo.dev.api.code.domain.entity.CodeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Long> {

    @Query("SELECT new com.mildo.dev.api.code.domain.dto.request.CodeLevelDTO(c.problemEntity.problemLevel, COUNT(c)) FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId GROUP BY c.problemEntity.problemLevel")
    List<CodeLevelDTO> findSolvedProblemLevelCountByMemberId(@Param("memberId") String memberId);

    @Query("SELECT new com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO(c.codeNo, c.problemEntity.problemTitle, c.problemEntity.problemLevel, c.problemEntity.problemType, c.codeSolvedDate, c.codeTime)" +
            "FROM CodeEntity c JOIN c.problemEntity p WHERE c.memberEntity.memberId = :memberId ORDER BY c.codeSolvedDate DESC")
    List<CodeSolvedListDTO> findSolvedProblemListByMemberId(@Param("memberId") String memberId, Pageable pageable);

    @Query("SELECT new com.mildo.dev.api.code.domain.dto.request.CodeSolvedListDTO(c.codeNo, c.problemEntity.problemTitle, c.problemEntity.problemLevel, c.problemEntity.problemType, c.codeSolvedDate, c.codeTime)" +
            "FROM CodeEntity c JOIN c.problemEntity p WHERE c.memberEntity.memberId = :memberId AND (:title IS NULL OR c.problemEntity.problemTitle LIKE %:title%) ORDER BY c.codeSolvedDate DESC")
    List<CodeSolvedListDTO> findSolvedProblemListTitleByMemberId(@Param("memberId") String memberId, @Param("title") String title, Pageable pageable);

}
