package com.mildo.dev.api.user.customoauth.handler;

import com.mildo.dev.api.user.controller.UserController;
import com.mildo.dev.api.user.customoauth.dto.CustomUser;
import com.mildo.dev.api.user.domain.entity.UserEntity;
import com.mildo.dev.api.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public CustomOAuthUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 정보

        log.info("oAuth2User = {}", oAuth2User);

        String providerld = (String) oAuth2User.getAttributes().get("sub");
        String email = oAuth2User.getAttribute("email"); // 이메일
        String username = oAuth2User.getAttribute("name"); //이름

        UserEntity user = userRepository.findByName(username);
        if (user == null) {
            user = UserEntity.builder()
                    .name(username)
                    .email(email)
                    .build(); //TODO userId 생성 (랜덤한 6자리 고유 ID)
            userRepository.save(user);

            user = userRepository.findByName(username);
            log.info("회원 가입 성공");
        }

        return new CustomUser(user.getUserId(), user.getName(),  user.getEmail());
    }

}
