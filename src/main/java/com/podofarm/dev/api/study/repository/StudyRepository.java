package com.podofarm.dev.api.study.repository;

import com.podofarm.dev.api.study.domain.entity.StudyEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface StudyRepository extends JpaRepository<StudyEntity, String>, CustomStudyRepository {

    /*
    TODO MemberEntity, TokenEntity, StudyEntity 간 모든 연관관계가 지연로딩으로 설정되어 있고,
        직접적으로 TokenEntity 관련 데이터를 사용하는 부분이 없음에도 JPQL 쿼리를 다음과 같이 작성하면
        select token ... 쿼리가 member 수만큼 추가로 나가는 N+1 문제가 발생
        - select s from StudyEntity s join fetch s.memberEntityList where s.studyId = :studyId
        그래서 우선은 @EntityGraph 를 사용해서 쿼리가 한 번만 나가도록 했지만,이 방법을 써도
        study, member 만 조인하는 게 아니라 study, member, token 테이블을 모두 조인함.
        => 프록시의 한계로 인해 @OneToOne 에 설정한 지연 로딩이 제대로 적용되지 않기 떄문 (노션에 정리)
     */
    @EntityGraph(attributePaths = {"memberEntityList"})
    @Query("""
            select s
            from StudyEntity s
            join s.memberEntityList
            where s.studyId = :studyId
            """)
    Optional<StudyEntity> findByIdCascade(@Param("studyId") String studyId);

    /*
    TODO 파라미터로 String memberId만 넘기는 게 좋을까 MemberEntity member 객체를 통으로 넘기는 게 좋을까.
        뭔가 후자가 더 객체지향적인 것 같긴 함
     */
    @Modifying
    @Query("""
            delete from CodeEntity c
            where c.memberEntity.memberId = :memberId
            """)
    int deleteMemberCode(@Param("memberId") String memberId);

    @Modifying
    @Query("""
            delete from CommentEntity c
            where c.memberEntity.memberId = :memberId
            """)
    int deleteMemberComment(@Param("memberId") String memberId);

    @Modifying
    @Query("""
            delete from CodeEntity c
            where c.memberEntity.memberId in :memberIds
            """)
    int deleteAllMemberCode(@Param("memberIds") Set<String> memberIds);

    @Modifying
    @Query("""
            delete from CommentEntity c
            where c.memberEntity.memberId in :memberIds
            """)
    int deleteAllMemberComment(@Param("memberIds") Set<String> memberIds);

}
