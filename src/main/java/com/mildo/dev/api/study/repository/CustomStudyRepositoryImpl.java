package com.mildo.dev.api.study.repository;

import com.mildo.dev.api.study.repository.dto.CountingSolvedDto;
import com.mildo.dev.api.study.repository.dto.GrassInfoDto;
import com.mildo.dev.api.study.repository.dto.QCountingSolvedDto;
import com.mildo.dev.api.study.repository.dto.QGrassInfoDto;
import com.mildo.dev.api.study.repository.dto.QStudyInfoDto;
import com.mildo.dev.api.study.repository.dto.QStudyInfoDto_MemberDto;
import com.mildo.dev.api.study.repository.dto.StudyInfoDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.mildo.dev.api.code.domain.entity.QCodeEntity.codeEntity;
import static com.mildo.dev.api.member.domain.entity.QMemberEntity.memberEntity;
import static com.mildo.dev.api.study.domain.entity.QStudyEntity.studyEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

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
                                .and(codeEntity.codeSolvedDate.lt(startOfNextMonth))
                        //codeEntity.codeAnswer.eq("Y")
                )
                .groupBy(memberEntity.memberId, dayExpression)
                .orderBy(memberEntity.memberId.asc(), dayExpression.asc())
                .fetch();
    }

    @Override
    public List<CountingSolvedDto> countSolved(String studyId, YearMonth yearMonth) {
        NumberExpression<Integer> solved = numberTemplate(Integer.class, "cast({0} as integer)",
                count(codeEntity.codeNo));

        /*
        문제를 풀지 않은 스터디원의 정보도 결과에 포함될 수 있도록,
        solvedAt()과 codeEntity.codeAnswer.eq("Y") 조건을 where절이 아닌 on절에 위치시킴
        TODO 다만 데이터의 양이 많아지면 where절에 조건이 있는 것과 비교해서
            성능 차이가 날 수도 있기 때문에 추후 성능 테스트를 해 보는 게 좋을 듯
         */
        return query
                .select(new QCountingSolvedDto(
                        memberEntity.memberId,
                        memberEntity.name,
                        solved)
                )
                .from(memberEntity)
                .leftJoin(codeEntity)
                    .on(
                        codeEntity.memberEntity.eq(memberEntity),
                        solvedAt(yearMonth)
                        //codeEntity.codeAnswer.eq("Y")
                    )
                .where(memberEntity.studyEntity.studyId.eq(studyId))
                .groupBy(memberEntity.memberId)
                .orderBy(solved.desc(), memberEntity.name.asc())
                .fetch();
    }

    private BooleanExpression solvedAt(YearMonth yearMonth) {
        if (yearMonth == null) { //yearMonth 가 null 이면 금일 기준 그 전날까지 해결한 총 문제 수
            Timestamp startOfToday = Timestamp.valueOf(LocalDate.now().atStartOfDay()); //금일 자정
            return codeEntity.codeSolvedDate.lt(startOfToday);
        }

        //yearMonth 가 null 이 아니면 당년 당월 내에 해결한 문제 수
        Timestamp startOfThisMonth = Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay());
        Timestamp startOfNextMonth = Timestamp.valueOf(yearMonth.plusMonths(1).atDay(1).atStartOfDay());

        return codeEntity.codeSolvedDate.goe(startOfThisMonth)
                .and(codeEntity.codeSolvedDate.lt(startOfNextMonth));
    }
}
