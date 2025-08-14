package com.wino.wino_api.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void defaults(Model model) {
        if (!model.containsAttribute("activeMenu")) {
            model.addAttribute("activeMenu", ""); // 기본값으로 빈 문자열
        }
    }
}
