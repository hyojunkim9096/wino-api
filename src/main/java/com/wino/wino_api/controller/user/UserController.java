package com.wino.wino_api.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class UserController {

    /**
     * 로그인 페이지 이동
     */
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/flutter/login/login.html";
    }

    /**
     * 회원가입 페이지로 이동
     */
    @GetMapping("/signup")
    public String signupPage() {
        return "redirect:/flutter/sign/signup.html";
    }
}
