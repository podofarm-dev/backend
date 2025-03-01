package com.podofarm.dev.api.problem.repository;

import com.podofarm.dev.api.problem.repository.dto.ProblemListDslDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemRepositoryCustom {

    Page<ProblemListDslDto> findFilteredProblemList(String title, String category, String memberId, Pageable pageable);

}
