package com.mildo.dev.api.member.customoauth.dto;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@ToString
public class CustomUser implements OAuth2User {

    private final String memberId;
    private final String username;
    private final String email;

    public CustomUser(String memberId, String username, String email) {
        this.memberId = memberId;
        this.username = username;
        this.email = email;
    }

    public String getMemberId() {return memberId;}

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap(); // OAuth2User 요구사항, 비워둬도 무방
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 설정이 필요하다면 추가
    }

    @Override
    public String getName() {
        return memberId;
    }

}
