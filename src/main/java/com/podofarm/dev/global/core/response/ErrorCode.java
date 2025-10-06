package com.podofarm.dev.global.core.response;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // 회원 관련 에러
  MEMBER_REGISTER_FAILED("MEMBER_REGISTER_FAILED", "회원가입에 실패했습니다."),
  MEMBER_ROLE_UPDATE_FAILED("MEMBER_ROLE_UPDATE_FAILED", "회원 역할 변경에 실패했습니다."),
  MEMBER_LIST_FETCH_FAILED("MEMBER_LIST_FETCH_FAILED", "회원 목록 조회에 실패했습니다."),
  MANAGER3_LIST_FETCH_FAILED("MANAGER3_LIST_FETCH_FAILED", "MANAGER3 역할 회원 목록 조회에 실패했습니다."),

  // 인증 관련 에러
  SIGNUP_FAILED("SIGNUP_FAILED", "회원가입에 실패했습니다."),
  MANAGER3_SIGNUP_FAILED("MANAGER3_SIGNUP_FAILED", "Manager3 회원가입에 실패했습니다."),
  LOGIN_FAILED("LOGIN_FAILED", "로그인에 실패했습니다."),
  ADMIN_LOGIN_FAILED("ADMIN_LOGIN_FAILED", "관리자 로그인에 실패했습니다."),
  INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "RefreshToken이 유효하지 않거나 없습니다."),

  // 비밀번호 관련 에러
  INVALID_PASSWORD("INVALID_PASSWORD", "잘못된 비밀번호입니다."),
  PASSWORD_CHANGE_FAILED("PASSWORD_CHANGE_FAILED", "비밀번호 변경에 실패했습니다."),

  // 공지사항 관련 에러
  NOTICE_LIST_FETCH_FAILED("NOTICE_LIST_FETCH_FAILED", "공지사항 목록 조회에 실패했습니다."),

  // 안전관리 Task 관련 에러
  SAFETY_TASK_LIST_FAILED("SAFETY_TASK_LIST_FAILED", "월별 Task 목록 조회에 실패했습니다."),
  SAFETY_TASK_DETAIL_FAILED("SAFETY_TASK_DETAIL_FAILED", "Task 상세 조회에 실패했습니다."),
  SAFETY_TASK_GENERATE_FAILED("SAFETY_TASK_GENERATE_FAILED", "월별 Task 생성에 실패했습니다."),
  SAFETY_TASK_STATUS_UPDATE_FAILED("SAFETY_TASK_STATUS_UPDATE_FAILED", "Task 상태 업데이트에 실패했습니다."),
  SAFETY_TASK_DESCRIPTION_UPDATE_FAILED(
      "SAFETY_TASK_DESCRIPTION_UPDATE_FAILED", "Task 설명 업데이트에 실패했습니다."),
  SAFETY_TASK_TEMPLATE_LINK_FAILED(
      "SAFETY_TASK_TEMPLATE_LINK_FAILED", "DocumentTemplate 연결에 실패했습니다."),
  SAFETY_TASK_FILE_UPLOAD_FAILED("SAFETY_TASK_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
  SAFETY_TASK_DOCUMENT_UPDATE_FAILED("SAFETY_TASK_DOCUMENT_UPDATE_FAILED", "내역서 수정에 실패했습니다."),
  SAFETY_TASK_DEACTIVATE_FAILED("SAFETY_TASK_DEACTIVATE_FAILED", "Task 비활성화에 실패했습니다."),

  // 메일 관련 에러
  MAIL_SEND_FAILED("MAIL_SEND_FAILED", "메일 전송에 실패했습니다."),
  INVALID_EMAIL_ADDRESS("INVALID_EMAIL_ADDRESS", "유효하지 않은 이메일 주소입니다."),
  MAIL_TEMPLATE_NOT_FOUND("MAIL_TEMPLATE_NOT_FOUND", "메일 템플릿을 찾을 수 없습니다."),
  MAIL_CONFIGURATION_ERROR("MAIL_CONFIGURATION_ERROR", "메일 설정 오류가 발생했습니다."),
  MAIL_AUTHENTICATION_FAILED("MAIL_AUTHENTICATION_FAILED", "메일 서버 인증에 실패했습니다."),
  MAIL_ATTACHMENT_ERROR("MAIL_ATTACHMENT_ERROR", "메일 첨부파일 처리 중 오류가 발생했습니다."),

  // 공통 에러
  ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "요청한 데이터를 찾을 수 없습니다."),
  USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
  INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
  ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다."),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
