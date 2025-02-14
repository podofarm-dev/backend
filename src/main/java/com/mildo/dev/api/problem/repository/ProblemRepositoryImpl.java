//package com.mildo.dev.api.problem.repository;
//
//import com.mildo.dev.api.code.domain.entity.QCodeEntity;
//import com.mildo.dev.api.problem.domain.dto.response.ProblemListDto;
//import com.mildo.dev.api.problem.domain.entity.QProblemEntity;
//import com.mildo.dev.api.problem.repository.dto.QProblemListDto;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.core.types.dsl.CaseBuilder;
//import com.querydsl.jpa.JPQLTemplates;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//public class ProblemRepositoryImpl implements ProblemRepositoryCustom{
//
//    private final JPAQueryFactory queryFactory;
//
//    public ProblemRepositoryImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
//    }
//
//    @Override
//    public List<ProblemListDto> findAllProblemList(String memberId, Pageable pageable) {
//        QProblemEntity problem = QProblemEntity.problemEntity;
//        QCodeEntity code = QCodeEntity.codeEntity;
//
//        return queryFactory
//                .select(new QProblemListDto(
//                        problem.problemId,
//                        problem.problemTitle,
//                        problem.problemLevel,
//                        problem.problemLink,
//                        code.codeStatus
//                ))
//                .from(problem)
//                .leftJoin(code).on(problem.problemId.eq(code.problemId)
//                        .and(code.memberEntity.memberId.eq(memberId))) // memberId 조건 추가
//                .orderBy(problem.problemNo.asc()) // 정렬 추가
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
////    @Override
////    public List<ProblemListDto> findFilteredProblemList(String title, String category, String memberId, Pageable pageable) {
////        QProblemEntity problem = QProblemEntity.problemEntity;
////        QCodeEntity code = QCodeEntity.codeEntity;
////
////        BooleanBuilder whereClause = new BooleanBuilder();
////
////        // 문제 제목 검색 조건
////        if (title != null && !title.isEmpty()) {
////            whereClause.and(problem.problemTitle.contains(title));
////        }
////
////        // category 조건 (solved: 'Y', no-solved: NULL)
////        if ("solved".equals(category)) {
////            whereClause.and(code.codeStatus.eq("Y"));
////        } else if ("no-solved".equals(category)) {
////            whereClause.and(code.codeStatus.isNull());
////        }
////
////        // memberId 조건 추가
////        whereClause.and(code.memberEntity.memberId.eq(memberId));
////
////        // QueryDSL로 쿼리 작성
////        List<ProblemListDto> results = queryFactory
////                .select(new QProblemListDto(
////                        problem.problemId,
////                        problem.problemTitle,
////                        problem.problemLevel,
////                        problem.problemLink,
////                        code.codeStatus
////                ))
////                .from(problem)
////                .leftJoin(code).on(problem.problemId.eq(code.problemId))
////                .where(whereClause)
////                .orderBy(
////                        new CaseBuilder()
////                                .when(code.codeStatus.eq("Y")).then(1)
////                                .when(code.codeStatus.isNull()).then(2)
////                                .otherwise(3).asc(),
////                        problem.problemNo.asc()
////                )
////                .offset(pageable.getOffset())
////                .limit(pageable.getPageSize())
////                .fetch();
////
////        return results;
////    }
//}
