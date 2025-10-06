package com.podofarm.dev.global.core.service;

import com.podofarm.dev.global.core.repository.BaseRepository;
import com.podofarm.dev.global.core.util.search.SearchCondition;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 공통 서비스 구현 클래스 - BaseService를 구현하여 기본 CRUD 로직 처리 - BaseRepository를 주입받아 실제 DB 처리 수행 - Entity ↔ DTO
 * 변환은 각 도메인 ServiceImpl에서 직접 구현
 *
 * <p>역할: - Controller와 DB 사이의 중간 처리 계층 - CRUD 반복 로직 제거 (create, update, delete, findById 등) - 재사용성과
 * 유지보수 용이성 향상
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

  protected final BaseRepository<T, Long> repository;

  protected BaseServiceImpl(BaseRepository<T, Long> repository) {
    this.repository = repository;
  }

  @Override
  public T create(T entity) {
    return repository.save(entity);
  }

  @Override
  public T update(T entity) {
    return repository.save(entity);
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id); // 추후 soft delete로 교체 가능
  }

  @Override
  public T findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<SearchCondition> convertFilters(Map<String, Object> filters) {
    return filters.entrySet().stream()
        .map(entry -> new SearchCondition(entry.getKey(), entry.getValue()))
        .toList();
  }

  @Override
  public Page<T> search(Map<String, Object> filters, Pageable pageable) {
    return repository.findAll(pageable); // 필요시 조건부 검색으로 확장 가능
  }


  @Override
  public void softDelete(T entity) {
    try {
      java.lang.reflect.Field isDeleteField = entity.getClass().getDeclaredField("isDelete");
      isDeleteField.setAccessible(true);
      isDeleteField.set(entity, true);
      repository.save(entity);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Entity does not have isDelete field or cannot access it", e);
    }
  }
}
