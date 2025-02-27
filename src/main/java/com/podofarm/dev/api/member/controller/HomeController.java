package com.podofarm.dev.api.member.controller;

import com.podofarm.dev.api.member.customoauth.dto.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/dev-login")
    public RedirectView login() {
        String redirectUrl = "/oauth2/authorization/google";
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/loginSuccess")
    public RedirectView loginSuccess(@AuthenticationPrincipal CustomUser customUser) {
        String social = "google";
        String redirectUrl = "http://podofarm.xyz/social-login/" + social + "?memberId=" + customUser.getName();
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/loginFailure")
    public RedirectView loginFailure(@RequestParam(required = false) String error){
        log.info("error = {}", error);

        String redirectUrl = "http://podofarm.xyz/social-login/fail";
        return new RedirectView(redirectUrl);
    }

}
