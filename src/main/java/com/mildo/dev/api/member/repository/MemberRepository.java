package com.mildo.dev.api.member.repository;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    MemberEntity findByGoogleId(String googleId);

    Optional<MemberEntity> findByUserId(String userId);
}
