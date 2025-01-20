package com.mildo.dev.api.user.customoauth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUser implements OAuth2User {

    private final String userId;
    private final String username;
    private final String email;

    public CustomUser(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {return userId;}

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
        return username;
    }

//    @Override
//    public String toString() {
//        return "CustomUser{" +
//                "userid=" + userid +
//                ", username='" + username + '\'' +
//                ", nickname='" + nickname + '\'' +
//                ", email='" + email + '\'' +
//                '}';
//    }

}
