package com.podofarm.dev.api.problem.repository;

import com.podofarm.dev.api.code.domain.entity.QCodeEntity;
import com.podofarm.dev.api.problem.domain.entity.QProblemEntity;
import com.podofarm.dev.api.problem.repository.dto.ProblemListDslDto;
import com.podofarm.dev.api.problem.repository.dto.QProblemListDslDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProblemRepositoryImpl implements ProblemRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ProblemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    public Page<ProblemListDslDto> findFilteredProblemList(String title, String category, String memberId, Pageable pageable) {
        QProblemEntity problem = QProblemEntity.problemEntity;
        QCodeEntity code = QCodeEntity.codeEntity;

        BooleanBuilder whereClause = new BooleanBuilder();

        if (title != null && !title.isEmpty()) {
            whereClause.and(problem.problemTitle.contains(title));
        }
        if ("Y".equals(category)) {
            whereClause.and(code.codeStatus.isTrue());
        } else if ("N".equals(category)) {
            whereClause.and(code.codeStatus.isFalse());
        }

        BooleanBuilder joinCondition = new BooleanBuilder();
        if (memberId != null && !memberId.isEmpty()) {
            joinCondition.and(code.memberEntity.memberId.eq(memberId));
        }

        OrderSpecifier<?> orderSpecifier = problem.problemNo.asc();

        if ("Y".equals(category)) {
            orderSpecifier = code.codeSolvedDate.asc();
        }

        // QueryDSL로 쿼리 작성
        JPAQuery<ProblemListDslDto> query = queryFactory
                .select(new QProblemListDslDto(
                        problem.problemNo,
                        problem.problemId,
                        problem.problemTitle,
                        problem.problemLevel,
                        problem.problemLink,
                        code.codeStatus
                ))
                .from(problem)
                .leftJoin(code).on(problem.problemId.eq(code.problemEntity.problemId).and(joinCondition)) // 동적 JOIN 조건 추가
                .where(whereClause)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 전체 데이터 개수 조회
        long total = queryFactory
                .select(problem.count())
                .from(problem)
                .leftJoin(code).on(problem.problemId.eq(code.problemEntity.problemId).and(joinCondition))
                .where(whereClause)
                .fetchOne();

        // 데이터 목록 조회
        List<ProblemListDslDto> content = query.fetch();

        // Page 객체로 래핑하여 반환
        return new PageImpl<>(content, pageable, total);
    }

}

