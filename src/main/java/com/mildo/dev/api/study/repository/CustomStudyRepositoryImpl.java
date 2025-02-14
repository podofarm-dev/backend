package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.repository.dto.GrassInfoDto;
import com.mildo.dev.api.study.repository.dto.QGrassInfoDto;
import com.mildo.dev.api.study.repository.dto.QStudyInfoDto;
import com.mildo.dev.api.study.repository.dto.QStudyInfoDto_MemberDto;
import com.mildo.dev.api.study.repository.dto.StudyInfoDto;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.List;

import static com.mildo.dev.api.code.domain.entity.QCodeEntity.codeEntity;
import static com.mildo.dev.api.member.domain.entity.QMemberEntity.memberEntity;
import static com.mildo.dev.api.study.domain.entity.QStudyEntity.studyEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Slf4j
public class CustomStudyRepositoryImpl implements CustomStudyRepository {

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
                                                memberEntity.imgUrl,
                                                memberEntity.leader
                                        ))
                                )
                        )
                )
                .get(0);
    }

    @Override
    public List<GrassInfoDto> countSolvedPerDate(String studyId, YearMonth yearMonth) {
        Timestamp startOfThisMonth = Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay());
        Timestamp startOfNextMonth = Timestamp.valueOf(yearMonth.plusMonths(1).atDay(1).atStartOfDay());

        NumberTemplate<Integer> dayExpression = numberTemplate(
                Integer.class, "extract(day from {0})", codeEntity.codeSolvedDate
        );

        log.info("startOfThisMonth={}", startOfThisMonth);
        log.info("startOfNextMonth={}", startOfNextMonth);

        return query
                .select(new QGrassInfoDto(
                        memberEntity.memberId,
                        dayExpression,
                        codeEntity.count().intValue())
                )
                .from(codeEntity)
                .leftJoin(codeEntity.memberEntity, memberEntity)
                .where(memberEntity.studyEntity.studyId.eq(studyId),
                        codeEntity.codeSolvedDate.goe(startOfThisMonth)
                                .and(codeEntity.codeSolvedDate.lt(startOfNextMonth)),
                        codeEntity.codeAnswer.eq("Y")
                )
                .groupBy(memberEntity.memberId, dayExpression)
                .orderBy(memberEntity.memberId.asc(), dayExpression.asc())
                .fetch();
    }
}
