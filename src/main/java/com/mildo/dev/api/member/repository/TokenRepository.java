package com.mildo.dev.api.member.repository;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.domain.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByMemberEntity_MemberId(String memberId);

    Optional<TokenEntity> findByMemberEntityMemberIdAndAccessToken(String memberId, String accessToken);

}
