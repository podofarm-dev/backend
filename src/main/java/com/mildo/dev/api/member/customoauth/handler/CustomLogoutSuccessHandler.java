package com.mildo.dev.api.member.customoauth.handler;

import com.mildo.dev.api.member.domain.entity.TokenEntity;
import com.mildo.dev.api.member.repository.TokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Transactional
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutSuccessHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication != null){
            String memberId = authentication.getName();
            log.info("memberId = {}", memberId);

            Optional<TokenEntity> tokenEntity = tokenRepository.findByMemberEntity_MemberId(memberId);

            TokenEntity token;
            if (tokenEntity.isPresent()) {
                token = tokenEntity.get(); // 객체 가져오기
                token.setRefreshToken("null");
                token.setAccessToken("null");
                token.setRefreshExpirationTime(null);
                tokenRepository.save(token);
            }
        }
        response.sendRedirect("/");
    }

}
