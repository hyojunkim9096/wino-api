package com.wino.wino_api.controller.admin.sign;

import com.wino.wino_api.dto.admin.AdminSignupDto;
import com.wino.wino_api.service.admin.AdminUserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 신규등록(Sign Up) 전용 컨트롤러
 */
@Controller
@RequestMapping("/admin/sign")
@RequiredArgsConstructor
public class AdminSignupController {

    private final AdminUserAuthService authService;

    /**
     * GET  /admin/sign/adminSignup
     * 신규등록 폼 표시
     */
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("adminSignupDto", new AdminSignupDto());
        return "admin/sign/adminSignup";
    }

    /**
     * POST /admin/sign/adminSignup
     * 폼에서 입력된 값으로 관리자 계정 생성
     */
    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("adminSignupDto") AdminSignupDto dto,
            BindingResult binding,
            Model model
    ) {
        if (binding.hasErrors()) {
            return "admin/sign/adminSignup";
        }
        authService.register(dto, "system");  // 실제 운영 시에는 인증된 관리자 ID를 넣어주세요
        return "redirect:/admin/login?registered";
    }
}