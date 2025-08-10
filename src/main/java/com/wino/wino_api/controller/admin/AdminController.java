package com.wino.wino_api.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 관리자 로그인 화면
     */
    @GetMapping("/login")
    public String adminLogin() {
        return "admin/adminLogin";   //templates/admin/login.html
    }

    @GetMapping("/logout")
    public String adminLogout() { return "admin/adminLogin"; }

}
