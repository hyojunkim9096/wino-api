// src/main/java/com/wino/wino_api/controller/admin/board/AdminMainBoardController.java
package com.wino.wino_api.controller.admin.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/board")
public class AdminMainBoardController {

    /**
     * 로그인 성공 시 첫 페이지
     * URL: /admin/board/adminMainBoard
     */
    @GetMapping("/adminMainBoard")
    public String adminMainBoard(Model model) {
        // 좌측 사이드바 활성화
        model.addAttribute("activeMenu", "dashboard");
        return "admin/board/adminMainBoard";
    }
}
