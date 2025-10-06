package com.podofarm.dev.global.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
  private String timestamp;
  private int status;
  private T data;
  private String errorCode;
  private String errorDetail;

  public static String now() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public static <T> ResponseEntity<BaseResponse<T>> successResponse(T data) {
    return ResponseEntity.ok(
        BaseResponse.<T>builder().timestamp(now()).status(200).data(data).build());
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(
      String errorCode, String errorDetail) {
    return ResponseEntity.badRequest()
        .body(
            BaseResponse.<T>builder()
                .timestamp(now())
                .status(400)
                .errorCode(errorCode)
                .errorDetail(errorDetail)
                .build());
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(
      int status, String errorCode, String errorDetail) {
    return ResponseEntity.status(status)
        .body(
            BaseResponse.<T>builder()
                .timestamp(now())
                .status(status)
                .errorCode(errorCode)
                .errorDetail(errorDetail)
                .build());
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(ErrorCode errorCode) {
    return errorResponse(400, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(
      ErrorCode errorCode, String additionalDetail) {
    return errorResponse(
        400, errorCode.getCode(), errorCode.getMessage() + ": " + additionalDetail);
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(int status, ErrorCode errorCode) {
    return errorResponse(status, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> errorResponse(
      int status, ErrorCode errorCode, String additionalDetail) {
    return errorResponse(
        status, errorCode.getCode(), errorCode.getMessage() + ": " + additionalDetail);
  }

  // 404 Not Found
  public static <T> ResponseEntity<BaseResponse<T>> notFoundResponse(ErrorCode errorCode) {
    return errorResponse(404, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> notFoundResponse(
      String errorCode, String errorDetail) {
    return errorResponse(404, errorCode, errorDetail);
  }

  // 500 Internal Server Error
  public static <T> ResponseEntity<BaseResponse<T>> internalServerErrorResponse(
      ErrorCode errorCode) {
    return errorResponse(500, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> internalServerErrorResponse(
      ErrorCode errorCode, String additionalDetail) {
    return errorResponse(
        500, errorCode.getCode(), errorCode.getMessage() + ": " + additionalDetail);
  }

  public static <T> ResponseEntity<BaseResponse<T>> internalServerErrorResponse(
      String errorCode, String errorDetail) {
    return errorResponse(500, errorCode, errorDetail);
  }

  // 401 Unauthorized
  public static <T> ResponseEntity<BaseResponse<T>> unauthorizedResponse(ErrorCode errorCode) {
    return errorResponse(401, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> unauthorizedResponse(
      String errorCode, String errorDetail) {
    return errorResponse(401, errorCode, errorDetail);
  }

  // 403 Forbidden
  public static <T> ResponseEntity<BaseResponse<T>> forbiddenResponse(ErrorCode errorCode) {
    return errorResponse(403, errorCode.getCode(), errorCode.getMessage());
  }

  public static <T> ResponseEntity<BaseResponse<T>> forbiddenResponse(
      String errorCode, String errorDetail) {
    return errorResponse(403, errorCode, errorDetail);
  }

  public static ResponseEntity<byte[]> pdf(byte[] pdfBytes, String fileName) {
    // Remove duplicate .pdf extension if it already exists
    String cleanFileName = fileName.toLowerCase().endsWith(".pdf") ? fileName : fileName + ".pdf";

    // Create Content-Disposition header with proper UTF-8 encoding
    String contentDisposition = createContentDispositionHeader(cleanFileName);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentLength(pdfBytes.length)
        .body(pdfBytes);
  }

  private static String createContentDispositionHeader(String fileName) {
    try {
      // URL encode the filename using UTF-8
      String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

      // Replace + with %20 for proper space encoding in filenames
      encodedFileName = encodedFileName.replace("+", "%20");

      // Use RFC 5987 format for UTF-8 encoded filenames
      // This format: attachment; filename*=UTF-8''encoded_filename
      // Also include fallback filename for older browsers
      return String.format(
          "attachment; filename=\"%s\"; filename*=UTF-8''%s",
          fileName.replaceAll("[^\\x00-\\x7F]", "?"), // ASCII fallback
          encodedFileName);
    } catch (UnsupportedEncodingException e) {
      // Fallback to simple format if encoding fails
      return "attachment; filename=\"" + fileName.replaceAll("[^\\x00-\\x7F]", "?") + "\"";
    }
  }
}
