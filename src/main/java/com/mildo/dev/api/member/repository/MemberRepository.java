package com.mildo.dev.api.member.repository;

import com.mildo.dev.api.member.domain.dto.ProblemMemberDto;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    MemberEntity findByGoogleId(String googleId);

    Optional<MemberEntity> findByMemberId(String memberId);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.studyEntity.studyId = :studyId")
    long countMembersByStudyId(@Param("studyId") String studyId);

    @Query("SELECT new com.mildo.dev.api.member.domain.dto.ProblemMemberDto(m.name, m.memberId, m.imgUrl, COUNT(c.codeNo)) " +
            "FROM MemberEntity m LEFT JOIN CodeEntity c ON m.memberId = c.memberEntity.memberId " +
            "WHERE m.memberId = :memberId " +
            "GROUP BY m.name, m.memberId, m.imgUrl")
    ProblemMemberDto countProblemByMemberId(@Param("memberId") String memberId);
}