package com.mildo.dev.api.utils.cookie;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // 클라이언트에서 접근 불가 JS로 해킹하는데 그걸 막아줌
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/"); // 애플리케이션 전체에서 접근 가능
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간
//        cookie.setDomain(); // 도메인 설정
        cookie.setAttribute("SameSite", "None"); // SameSite 설정
        return cookie;
    }

}
