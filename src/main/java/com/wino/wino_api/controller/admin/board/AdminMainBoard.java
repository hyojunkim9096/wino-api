package com.wino.wino_api.controller.admin.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/board")
public class AdminMainBoard {

    /**
     * 로그인 성공시 첫 페이지
     */
    @GetMapping("/adminMainBoard")
    public String adminMainBoard() {

        return "admin/board/adminMainBoard";
    }
}
