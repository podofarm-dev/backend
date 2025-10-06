package com.podofarm.dev.global.core.service;

import com.podofarm.dev.global.core.util.search.SearchCondition;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 공통 서비스 인터페이스 - CRUD 메서드 시그니처 정의 - 도메인별 Service 인터페이스에서 확장
 *
 * <p>역할: - 공통 동작 형식 통일 - 테스트/DI를 위한 계약 역할 수행
 */
public interface BaseService<T> {
  T create(T entity);

  T update(T entity);

  void delete(Long id);

  T findById(Long id);

  Page<T> search(Map<String, Object> filters, Pageable pageable);

  List<SearchCondition> convertFilters(Map<String, Object> filters);


  void softDelete(T entity);
}
