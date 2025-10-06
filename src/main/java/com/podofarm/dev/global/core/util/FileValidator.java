package com.podofarm.dev.global.core.util;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileValidator {

  // 허용되는 파일 확장자 목록
  @Value(
      "${file.upload.allowed-extensions:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,hwp}")
  private String allowedExtensions;

  // 최대 파일 크기 (기본값: 10MB)
  @Value("${file.upload.max-size:10485760}")
  private long maxFileSize;

  // 허용되는 MIME 타입 목록
  private static final List<String> ALLOWED_MIME_TYPES =
      Arrays.asList(
          "image/jpeg",
          "image/jpg",
          "image/png",
          "image/gif",
          "application/pdf",
          "application/msword",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          "application/vnd.ms-excel",
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          "application/vnd.ms-powerpoint",
          "application/vnd.openxmlformats-officedocument.presentationml.presentation",
          "text/plain",
          "application/x-hwp",
          "application/haansofthwp");

  /**
   * 파일 유효성 검사
   *
   * @param file 업로드할 파일
   * @return 유효성 검사 결과
   */
  public boolean isValid(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.warn("파일이 비어있습니다.");
      return false;
    }

    // 파일 크기 검사
    if (!isValidFileSize(file)) {
      return false;
    }

    // 파일 확장자 검사
    if (!isValidExtension(file)) {
      return false;
    }

    // MIME 타입 검사
    if (!isValidMimeType(file)) {
      return false;
    }

    return true;
  }

  /**
   * 파일 크기 검사
   *
   * @param file 검사할 파일
   * @return 크기 유효성 결과
   */
  private boolean isValidFileSize(MultipartFile file) {
    if (file.getSize() > maxFileSize) {
      log.warn(
          "파일 크기가 너무 큽니다. 최대 크기: {}MB, 현재 크기: {}MB",
          maxFileSize / (1024 * 1024),
          file.getSize() / (1024 * 1024));
      return false;
    }
    return true;
  }

  /**
   * 파일 확장자 검사
   *
   * @param file 검사할 파일
   * @return 확장자 유효성 결과
   */
  private boolean isValidExtension(MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (filename == null || filename.isEmpty()) {
      log.warn("파일명이 없습니다.");
      return false;
    }

    String extension = getFileExtension(filename).toLowerCase();
    List<String> allowedExtensionList = Arrays.asList(allowedExtensions.split(","));

    if (!allowedExtensionList.contains(extension)) {
      log.warn("허용되지 않는 파일 확장자입니다. 확장자: {}, 허용 확장자: {}", extension, allowedExtensions);
      return false;
    }

    return true;
  }

  /**
   * MIME 타입 검사
   *
   * @param file 검사할 파일
   * @return MIME 타입 유효성 결과
   */
  private boolean isValidMimeType(MultipartFile file) {
    String mimeType = file.getContentType();
    if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
      log.warn("허용되지 않는 MIME 타입입니다. MIME 타입: {}", mimeType);
      return false;
    }
    return true;
  }

  /**
   * 파일 확장자 추출
   *
   * @param filename 파일명
   * @return 확장자 (점 제외)
   */
  private String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
  }

  /**
   * 파일 유효성 검사 예외 메시지 반환
   *
   * @param file 검사할 파일
   * @return 유효성 검사 실패 시 에러 메시지
   */
  public String getValidationErrorMessage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return "파일이 비어있습니다.";
    }

    if (!isValidFileSize(file)) {
      return String.format("파일 크기가 너무 큽니다. 최대 크기: %dMB", maxFileSize / (1024 * 1024));
    }

    if (!isValidExtension(file)) {
      return String.format("허용되지 않는 파일 확장자입니다. 허용 확장자: %s", allowedExtensions);
    }

    if (!isValidMimeType(file)) {
      return "허용되지 않는 파일 타입입니다.";
    }

    return "파일이 유효합니다.";
  }
}
