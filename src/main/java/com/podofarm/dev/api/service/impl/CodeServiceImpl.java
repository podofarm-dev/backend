package com.podofarm.dev.api.service.impl;

import com.podofarm.dev.api.domain.Code;
import com.podofarm.dev.api.repository.CodeRepository;
import com.podofarm.dev.api.service.CodeService;
import com.podofarm.dev.global.core.service.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 코드 서비스 구현 클래스
 * BaseServiceImpl을 상속받아 기본 CRUD 로직 제공
 */
@Slf4j
@Service
public class CodeServiceImpl extends BaseServiceImpl<Code> implements CodeService {

    private final CodeRepository codeRepository;

    public CodeServiceImpl(CodeRepository repository) {
        super(repository);
        this.codeRepository = repository;
    }

    // TODO: 커스텀 비즈니스 로직 구현
    // 예: 코드 분석, AI 연동, 댓글 관리 등
}

