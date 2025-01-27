package com.mildo.dev.global.oauth.jwt.filter;

import com.mildo.dev.api.member.domain.entity.TokenEntity;
import com.mildo.dev.api.member.repository.TokenRepository;
import com.mildo.dev.global.oauth.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = JwtTokenProvider.SECRET_KEY;

    @Autowired
    private TokenRepository tokenRepository;

    public JwtFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // AntPathMatcher 으로 쉽게 동적 경로를 처리할 수 있다.
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 인증이 필요 없는 URL
        if (pathMatcher.match("/", requestURI)||
                pathMatcher.match("/loginSuccess", requestURI) ||
                pathMatcher.match("/loginFailure", requestURI) ||
                pathMatcher.match("/dev-login", requestURI) ||
                pathMatcher.match("/tokens", requestURI) ||
                pathMatcher.match("/tokens/refresh", requestURI) ||
                pathMatcher.match("/logout", requestURI) ||
                requestURI.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 토큰 추출
        String token = request.getHeader("Authorization");

        if(token == null){
            // token이 없음 잘못된 문법으로 인하여 서버가 요청을 히해할 수 없음
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Access Token is missing");
            return;
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                // 토큰 검증
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                Optional<TokenEntity> getToken = tokenRepository.findByMemberEntityMemberIdAndAccessToken(claims.getSubject(), token);

                if (getToken.isEmpty()) { // getToken이 없을 경우
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } catch (ExpiredJwtException e) { // Access Token 만료 시 발생
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;

            }catch (Exception e) {
                log.error("Exception e = {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access Token UNAUTHORIZED");
                return;
            }
        }

        // 다음 필터 호출 또는 컨트롤러 실행
        filterChain.doFilter(request, response);
    }

}
