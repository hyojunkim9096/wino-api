package com.wino.wino_api.controller.admin.sign;

import com.wino.wino_api.service.auth.PasswordResetByCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sign")
@RequiredArgsConstructor
public class PasswordResetByCodeController {

    private final PasswordResetByCodeService svc;

    @GetMapping("/forgot-password")
    public String forgotForm() { return "admin/sign/forgot-password"; }

    @PostMapping("/forgot-password")
    public String forgotSubmit(@RequestParam String userId, HttpServletRequest req, Model model) {
        String uid = userId == null ? "" : userId.trim();
        svc.issueCodeByUserId(uid, req.getRemoteAddr());
        model.addAttribute("userId", uid);
        return "admin/sign/forgot-password-code";
    }

    @GetMapping("/verify-code")
    public String verifyForm(@RequestParam String userId, Model model) {
        model.addAttribute("userId", userId == null ? "" : userId.trim());
        return "admin/sign/verify-code";
    }

    @PostMapping("/reset-password-by-code")
    public String resetByCode(@RequestParam String userId,
                              @RequestParam String code,
                              @RequestParam String password,
                              @RequestParam String passwordConfirm,
                              Model model) {

        String pwd = password == null ? "" : password;
        String pwd2 = passwordConfirm == null ? "" : passwordConfirm;

        if (!StringUtils.hasText(pwd) || pwd.length() < 8) {
            model.addAttribute("error", "비밀번호는 최소 8자 이상이어야 합니다.");
            model.addAttribute("userId", userId);
            return "admin/sign/verify-code";
        }
        if (!pwd.equals(pwd2)) {
            model.addAttribute("error", "비밀번호가 서로 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            return "admin/sign/verify-code";
        }

        String normalizedCode = code == null ? "" : code.replaceAll("\\s+", "");
        if (!normalizedCode.matches("\\d{6}")) {
            model.addAttribute("error", "코드 형식이 올바르지 않습니다. (숫자 6자리)");
            model.addAttribute("userId", userId);
            return "admin/sign/verify-code";
        }

        String uid = userId == null ? "" : userId.trim();
        try {
            svc.resetByCode(uid, normalizedCode, pwd);
            return "admin/sign/reset-password-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", uid);
            return "admin/sign/verify-code";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/sign/reset-password-error";
        }
    }
}
