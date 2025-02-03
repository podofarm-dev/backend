package com.mildo.dev.api.member.customoauth.handler;

import com.mildo.dev.api.member.controller.MemberController;
import com.mildo.dev.api.member.customoauth.dto.CustomUser;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import com.mildo.dev.api.member.repository.MemberRepository;
import com.mildo.dev.api.utils.random.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberRepository memberRepository;

    public CustomOAuthUserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Value("${BASIC_URL}")
    private String basic;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 정보
        log.info("oAuth2User = {}", oAuth2User);

        String googleId = (String) oAuth2User.getAttributes().get("sub");
        String email = oAuth2User.getAttribute("email"); // 이메일
        String username = oAuth2User.getAttribute("name"); //이름

        MemberEntity member = memberRepository.findByGoogleId(googleId);
        if (member == null) {
            String memberId;

            do {
                memberId = CodeGenerator.generateUserId();
            } while (memberRepository.findByMemberId(memberId).isPresent());

            member = MemberEntity.builder()
                    .memberId(memberId)
                    .name(username)
                    .googleId(googleId)
                    .email(email)
                    .leader("N")
                    .imgUrl(basic)
                    .build();
            memberRepository.save(member);

            member = memberRepository.findByGoogleId(googleId);
            log.info("회원 가입 성공");
        }

        return new CustomUser(member.getMemberId(), member.getName(), member.getEmail());
    }

}
