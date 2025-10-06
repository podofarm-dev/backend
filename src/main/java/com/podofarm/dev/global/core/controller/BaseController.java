package com.podofarm.dev.global.core.controller;

import com.podofarm.dev.global.core.response.BaseResponse;
import com.podofarm.dev.global.core.response.ErrorCode;
import com.podofarm.dev.global.core.response.SuccessCode;
import com.podofarm.dev.global.core.service.BaseService;
import com.podofarm.dev.global.core.util.common.Identifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공통 CRUD 기능을 제공하는 추상 컨트롤러 클래스입니다.
 *
 * <p>제네릭을 활용하여 다양한 도메인(Entity/DTO)에 대해 재사용할 수 있으며,
 * 공통 로직(create, update, delete, soft delete, get, search)을 제공하여 중복 코드를 제거합니다.
 *
 * <p>{@code E}는 JPA Entity 클래스, {@code D}는 Identifiable을 구현한 DTO 클래스입니다.
 * ModelMapper를 활용하여 Entity ↔ DTO 간 변환을 처리합니다.
 *
 * <p>기본 제공 엔드포인트:
 * <ul>
 *   <li>POST /create - 엔티티 생성</li>
 *   <li>PATCH /update - 엔티티 수정</li>
 *   <li>DELETE /{id} - 엔티티 Hard Delete (물리적 삭제)</li>
 *   <li>PATCH /soft-delete/{id} - 엔티티 Soft Delete (논리적 삭제, isDelete 필드 필요)</li>
 *   <li>GET /{id} - ID로 단건 조회</li>
 *   <li>GET /search - 검색 조건 + 페이징 조회</li>
 * </ul>
 *
 * <p>상속 시 필수 구현 메서드:
 * <ul>
 *   <li>{@link #getDtoClass()} - DTO 클래스 타입 반환</li>
 *   <li>{@link #getEntityClass()} - Entity 클래스 타입 반환</li>
 * </ul>
 *
 * <p>DTO 요구사항:
 * <ul>
 *   <li>{@link Identifiable} 인터페이스 구현 필수 (getId() 메서드 제공)</li>
 * </ul>
 *
 * @param <E> Entity 타입
 * @param <D> DTO 타입 (Identifiable 구현 필수)
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseController<E, D extends Identifiable> {

  protected final BaseService<E> service;
  protected final ModelMapper modelMapper;

  /** 생성 요청 */
  @PostMapping("/create")
  public ResponseEntity<BaseResponse<D>> create(@RequestBody D dto) {
    E entity = toEntity(dto);
    E saved = service.create(entity);
    D savedDto = toDto(saved);
    return successResponse(savedDto);
  }

  /** 수정 요청 */
  @PatchMapping("/update")
  public ResponseEntity<BaseResponse<D>> update(@RequestBody D dto) {
    E existing = service.findById(dto.getId());
    modelMapper.map(dto, existing);
    E updated = service.update(existing);
    D updatedDto = toDto(updated);
    return successResponse(updatedDto);
  }

  /** 삭제 요청 (id 기반) */
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
    service.delete(id);
    return successResponse(SuccessCode.DELETE_SUCCESS.getMessage());
  }


  /** Soft Delete 요청 (id 기반) */
  @PatchMapping("/soft-delete/{id}")
  public ResponseEntity<BaseResponse<String>> isDelete(@PathVariable Long id) {
    E entity = service.findById(id);
    service.softDelete(entity);
    return successResponse("Soft delete completed successfully");
  }

  /** 단건 조회 */
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<D>> get(@PathVariable Long id) {
    E entity = service.findById(id);
    if (entity == null) {
      return errorResponse("NOT_FOUND", "Not found with id=" + id);
    }
    return successResponse(toDto(entity));
  }

  /** 검색 + 페이징 */
  @GetMapping("/search")
  public ResponseEntity<BaseResponse<Page<D>>> search(
      @RequestParam Map<String, Object> filters, Pageable pageable) {
    Page<E> result = service.search(filters, pageable);
    Page<D> dtoResult = toDtoPage(result);
    return successResponse(dtoResult);
  }

  /** Entity → DTO 변환 */
  protected D toDto(E entity) {
    return modelMapper.map(entity, getDtoClass());
  }

  /** DTO → Entity 변환 */
  protected E toEntity(D dto) {
    return modelMapper.map(dto, getEntityClass());
  }

  /** Entity 리스트 → DTO 리스트 */
  protected List<D> toDtoList(List<E> entities) {
    List<D> dtos = new ArrayList<>();
    for (E entity : entities) {
      dtos.add(toDto(entity));
    }
    return dtos;
  }

  /** Entity Page → DTO Page */
  protected Page<D> toDtoPage(Page<E> entityPage) {
    return entityPage.map(this::toDto);
  }

  /** DTO 클래스 반환 */
  protected abstract Class<D> getDtoClass();

  /** Entity 클래스 반환 */
  protected abstract Class<E> getEntityClass();

  /** 성공 응답 생성 */
  protected <T> ResponseEntity<BaseResponse<T>> successResponse(T data) {
    return BaseResponse.successResponse(data);
  }

  /** 실패 응답 생성 */
  protected <T> ResponseEntity<BaseResponse<T>> errorResponse(
      String errorCode, String errorDetail) {
    return BaseResponse.errorResponse(400, errorCode, errorDetail);
  }

  /** ErrorCode2 enum 기반 실패 응답 생성 */
  protected <T> ResponseEntity<BaseResponse<T>> errorResponse(ErrorCode errorCode) {
    return BaseResponse.errorResponse(errorCode);
  }

  /** ErrorCode enum 기반 실패 응답 생성 (추가 상세정보 포함) */
  protected <T> ResponseEntity<BaseResponse<T>> errorResponse(
      ErrorCode errorCode, String additionalDetail) {
    return BaseResponse.errorResponse(errorCode, additionalDetail);
  }

  /** 404 Not Found 응답 */
  protected <T> ResponseEntity<BaseResponse<T>> notFoundResponse(ErrorCode errorCode) {
    return BaseResponse.notFoundResponse(errorCode);
  }

  /** 500 Internal Server Error 응답 */
  protected <T> ResponseEntity<BaseResponse<T>> internalServerErrorResponse(ErrorCode errorCode) {
    return BaseResponse.internalServerErrorResponse(errorCode);
  }

  /** 401 Unauthorized 응답 */
  protected <T> ResponseEntity<BaseResponse<T>> unauthorizedResponse(ErrorCode errorCode) {
    return BaseResponse.unauthorizedResponse(errorCode);
  }

  /** 403 Forbidden 응답 */
  protected <T> ResponseEntity<BaseResponse<T>> forbiddenResponse(ErrorCode errorCode) {
    return BaseResponse.forbiddenResponse(errorCode);
  }
}
