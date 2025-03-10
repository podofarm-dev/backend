package com.podofarm.dev.api.code.repository;

import com.podofarm.dev.api.code.domain.dto.request.CodeSolvedListDTO;
import com.podofarm.dev.api.code.domain.entity.CodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Long> {

    @Query(value = "SELECT new com.podofarm.dev.api.code.domain.dto.request.CodeSolvedListDTO(" +
            "c.codeNo, c.problemEntity.problemId, c.problemEntity.problemTitle, c.problemEntity.problemLevel, " +
            "c.problemEntity.problemType, c.codeSolvedDate, c.codeTime) " +
            "FROM CodeEntity c JOIN c.problemEntity p " +
            "WHERE c.memberEntity.memberId = :memberId " +
            "ORDER BY c.codeSolvedDate DESC",
            countQuery = "SELECT COUNT(c) FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId")
    Page<CodeSolvedListDTO> findSolvedProblemListByMemberId(@Param("memberId") String memberId, Pageable pageable);

    @Query(value = "SELECT new com.podofarm.dev.api.code.domain.dto.request.CodeSolvedListDTO(" +
            "c.codeNo, c.problemEntity.problemId, c.problemEntity.problemTitle, c.problemEntity.problemLevel, " +
            "c.problemEntity.problemType, c.codeSolvedDate, c.codeTime) " +
            "FROM CodeEntity c JOIN c.problemEntity p " +
            "WHERE c.memberEntity.memberId = :memberId " +
            "AND c.problemEntity.problemTitle LIKE %:title% " +
            "ORDER BY c.codeSolvedDate DESC",
            countQuery = "SELECT COUNT(c) FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId " +
                    "AND c.problemEntity.problemTitle LIKE %:title%")
    Page<CodeSolvedListDTO> findSolvedProblemListTitleByMemberId(@Param("memberId") String memberId, @Param("title") String title, Pageable pageable);

    @Query("SELECT c FROM CodeEntity c LEFT JOIN FETCH c.commentList WHERE c.codeNo = :codeNo")
    Optional<CodeEntity> findByIdWithComments(@Param("codeNo") Long codeNo);

    List<CodeEntity> findByMemberEntity_MemberIdAndProblemEntity_ProblemId(String memberId, Long problemId);

    @Modifying
    @Transactional
    @Query("UPDATE CodeEntity c SET c.codeSource = :code WHERE c.memberEntity.memberId = :memberId AND c.problemEntity.problemId = :problemId")
    int memberSolvedEdit(@Param("memberId") String memberId, @Param("problemId") String problemId, @Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId AND c.problemEntity.problemId = :problemId")
    int memberSolvedDelete(@Param("memberId") String memberId, @Param("problemId") String problemId);


    @Query("SELECT DISTINCT c.problemEntity.problemId FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId")
    List<Long> getProblemIdByMemberId(@Param("memberId") String memberId);

    @Query("SELECT c FROM CodeEntity c WHERE c.memberEntity.memberId = :memberId AND c.problemEntity.problemId = :problemId")
    Optional<CodeEntity> findByMemberIdAndProblemId(String memberId, Long problemId);
}
