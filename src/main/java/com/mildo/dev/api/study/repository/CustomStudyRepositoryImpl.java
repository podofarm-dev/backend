package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.repository.dto.QStudyInfoDto;
import com.mildo.dev.api.study.repository.dto.QStudyInfoDto_MemberDto;
import com.mildo.dev.api.study.repository.dto.StudyInfoDto;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static com.mildo.dev.api.member.domain.entity.QMemberEntity.memberEntity;
import static com.mildo.dev.api.study.domain.entity.QStudyEntity.studyEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

public class CustomStudyRepositoryImpl implements CustomStudyRepository{

    private final JPAQueryFactory query;

    public CustomStudyRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }


    @Override
    public StudyInfoDto searchStudyWithMembers(String studyId) {
        return query
                .from(studyEntity)
                .leftJoin(memberEntity)
                    .on(memberEntity.studyEntity.studyId.eq(studyEntity.studyId))
                .where(studyEntity.studyId.eq(studyId))
                .transform(
                        groupBy(studyEntity.studyId).list(
                                new QStudyInfoDto(
                                        studyEntity.studyId,
                                        studyEntity.studyName,
                                        studyEntity.studyStart,
                                        list(new QStudyInfoDto_MemberDto(
                                                memberEntity.memberId,
                                                memberEntity.name,
                                                memberEntity.leader
                                        ))
                                )
                        )
                )
                .get(0);

    }
}
