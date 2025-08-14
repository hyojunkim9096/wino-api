package com.wino.wino_api.controller.admin;

import com.wino.wino_api.dto.common.CommonCodeDto;
import com.wino.wino_api.entity.common.CommonCode;
import com.wino.wino_api.service.common.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/settings/common-code")
public class SettingsCommonCodeController {

    private final CommonCodeService service;

    /** 3:7 반응형 페이지 */
    @GetMapping
    public String page(Model model) {
        model.addAttribute("activeMenu", "settings-common-code"); // ✅ 활성 메뉴 지정
        model.addAttribute("groups", service.getTopGroups());
        return "admin/settings/common-code";
    }

    /** 상위 목록 */
    @GetMapping("/api/groups")
    @ResponseBody
    public List<CommonCode> groups() {
        return service.getTopGroups();
    }

    /** 하위 목록 */
    @GetMapping("/api/{parentId}/children")
    @ResponseBody
    public List<CommonCode> children(@PathVariable Long parentId) {
        return service.getChildren(parentId);
    }

    /** 생성(상위/하위 공통) */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody CommonCodeDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /** 수정 */
    @PatchMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CommonCodeDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /** 소프트 삭제 (delYn='Y') */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok().build();
    }
}
