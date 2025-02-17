package com.mildo.dev.api.problem.repository;

import com.mildo.dev.api.problem.repository.dto.ProblemListDslDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemRepositoryCustom {

    Page<ProblemListDslDto> findFilteredProblemList(String title, String category, String memberId, Pageable pageable);

}
