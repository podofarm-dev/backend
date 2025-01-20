package com.mildo.dev.api.user.repository;

import com.mildo.dev.api.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByName(String name);

    Optional<UserEntity> findByUserId(String userId);
}
