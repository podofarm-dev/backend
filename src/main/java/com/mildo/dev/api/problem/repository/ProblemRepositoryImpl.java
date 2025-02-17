package com.mildo.dev.api.problem.repository;

import com.mildo.dev.api.code.domain.entity.QCodeEntity;
import com.mildo.dev.api.problem.domain.entity.QProblemEntity;
import com.mildo.dev.api.problem.repository.dto.ProblemListDslDto;
import com.mildo.dev.api.problem.repository.dto.QProblemListDslDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
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

        // 문제 제목 검색 조건
        if (title != null && !title.isEmpty()) {
            whereClause.and(problem.problemTitle.contains(title));
        }

        // memberId 조건이 있을 때만 JOIN (불필요한 조인을 방지)
        BooleanBuilder joinCondition = new BooleanBuilder();
        if (memberId != null && !memberId.isEmpty()) {
            joinCondition.and(code.memberEntity.memberId.eq(memberId));
        }

        // 정렬 조건 설정
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if ("Y".equals(category)) {
            // status가 'Y'인 것을 먼저 정렬
            orderSpecifiers.add(new CaseBuilder()
                    .when(code.codeStatus.eq("Y")).then(1)
                    .otherwise(2).asc());
        } else if ("N".equals(category)) {
            // status가 NULL인 것을 먼저 정렬
            orderSpecifiers.add(new CaseBuilder()
                    .when(code.codeStatus.isNull()).then(1)
                    .otherwise(2).asc());
        }

        // 항상 problemNo 순으로 정렬
        orderSpecifiers.add(problem.problemNo.asc());

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
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // 동적 정렬 적용
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

