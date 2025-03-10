package com.podofarm.dev.api.member.repository;

import com.podofarm.dev.api.member.domain.dto.response.ProblemPageInfoResponse;
import com.podofarm.dev.api.member.domain.dto.response.SolvedMemberListResponse;
import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    MemberEntity findByGoogleId(String googleId);

    Optional<MemberEntity> findByMemberId(String memberId);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.studyEntity.studyId = :studyId")
    long countMembersByStudyId(@Param("studyId") String studyId);

    @Query("SELECT new com.podofarm.dev.api.member.domain.dto.response.ProblemPageInfoResponse(m.name, m.memberId, m.imgUrl, COUNT(c.codeNo)) " +
            "FROM MemberEntity m LEFT JOIN CodeEntity c ON m.memberId = c.memberEntity.memberId " +
            "WHERE m.memberId = :memberId " +
            "GROUP BY m.name, m.memberId, m.imgUrl")
    ProblemPageInfoResponse countProblemByMemberId(@Param("memberId") String memberId);

    @Query("SELECT new com.podofarm.dev.api.member.domain.dto.response.SolvedMemberListResponse(m.studyEntity.studyId, m.memberId, m.name, m.solvedProblem, m.imgUrl,0) " +
    "FROM MemberEntity m WHERE m.studyEntity.studyId = :studyId ")
    List<SolvedMemberListResponse> solvedMemberRanking(@Param("studyId") String studyId);


    @Query("SELECT COUNT(m) > 0 FROM MemberEntity m WHERE m.studyEntity.studyId = :studyId AND m.memberId = :memberId")
    boolean checkExtensionSync(@Param("memberId") String memberId, @Param("studyId") String studyId);


    @Query("""
            select m
            from MemberEntity m
            where m.studyEntity.studyId = :studyId and m.isParticipant <= :endOfMonth
            order by case when m.memberId = :loggedIn then 0 else 1 end, m.name asc
            """)
    List<MemberEntity> findInStudySorted(@Param("studyId") String studyId,
                                         @Param("loggedIn") String loggedIn,
                                         @Param("endOfMonth") Date endOfMonth);

    @Modifying
    @Query(value = "UPDATE member SET member_solvedproblem = member_solvedproblem + 1 WHERE member_id = :memberId", nativeQuery = true)
    @Transactional
    void incrementSolvedProblem(String memberId);
}