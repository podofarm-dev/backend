package com.podofarm.dev.api.utils.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final Logger log = LoggerFactory.getLogger(CookieUtil.class);

    private static String backDomain;

    public CookieUtil(Environment environment) {
        backDomain = environment.getProperty("domain.back");
    }

    public static Cookie createCookie(String name, String value, int maxAge) {
        log.info("backDomain={}", backDomain);

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // 클라이언트에서 접근 불가 JS로 해킹하는데 그걸 막아줌
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/"); // 애플리케이션 전체에서 접근 가능
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간
        cookie.setDomain(backDomain); // 도메인 설정
        cookie.setAttribute("SameSite", "None"); // SameSite 설정
        return cookie;
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        log.info("backDomain={}", backDomain);

        Cookie myCookie = new Cookie("RefreshToken", null);
        myCookie.setMaxAge(0); // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        myCookie.setPath("/");
        myCookie.setHttpOnly(true);
        myCookie.setSecure(true);
        myCookie.setDomain(backDomain);
        myCookie.setAttribute("SameSite", "None");
        response.addCookie(myCookie);
        response.setHeader("Set-Cookie", "RefreshToken=; Path=/; Domain=" + backDomain + "; Max-Age=0; Secure; HttpOnly; SameSite=None");
    }

}
