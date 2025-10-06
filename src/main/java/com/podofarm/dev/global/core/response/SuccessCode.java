package com.podofarm.dev.global.core.response;

public enum SuccessCode {

  // CRUD 관련 메시지
  CREATE_SUCCESS("Create was successful"),
  UPDATE_SUCCESS("Update was successful"),
  DELETE_SUCCESS("Delete was successful"),
  GET_SUCCESS("Get was successful"),

  // 인증 관련 메시지
  LOGIN_SUCCESS("Login was successful"),
  LOGOUT_SUCCESS("Logout was successful"),
  SIGNUP_SUCCESS("Sign up was successful"),

  // 승인 관련 메시지
  APPROVE_SUCCESS("Approve was successful"),
  REJECT_SUCCESS("Reject was successful"),

  // 기타 메시지
  PROCESS_SUCCESS("Process was successful");

  private final String message;

  SuccessCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }
}
