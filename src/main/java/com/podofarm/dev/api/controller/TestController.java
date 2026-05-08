package com.podofarm.dev.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Test", description = "테스트 API")
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "헬스 체크", description = "서버 상태를 확인합니다")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Health check requested");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Server is running");
        response.put("port", 8848);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스웨거 테스트", description = "Swagger가 정상 작동하는지 확인합니다")
    @GetMapping("/swagger-test")
    public ResponseEntity<String> swaggerTest() {
        log.info("Swagger test requested");
        return ResponseEntity.ok("Swagger is working!");
    }
}
