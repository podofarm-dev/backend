package com.podofarm.dev.api.member.repository;

import com.podofarm.dev.api.member.domain.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByMemberEntity_MemberId(String memberId);

    Optional<TokenEntity> findByMemberEntityMemberIdAndAccessToken(String memberId, String accessToken);

    Optional<TokenEntity> findByRefreshToken(String RefreshToken);

}
