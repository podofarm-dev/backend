package com.podofarm.dev.api.code.repository;

import com.podofarm.dev.api.code.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByCodeEntity_CodeNo(Long codeNo);
}
