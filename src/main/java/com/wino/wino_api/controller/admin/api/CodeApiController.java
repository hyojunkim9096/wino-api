// src/main/java/com/wino/wino_api/controller/admin/api/CodeApiController.java
package com.wino.wino_api.controller.admin.api;

import com.wino.wino_api.entity.common.CommonCode;
import com.wino.wino_api.service.common.CodeBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/api/codes")
@RequiredArgsConstructor
public class CodeApiController {

    private final CodeBookService codeBook;

    @GetMapping
    public Map<String, List<Map<String, Object>>> get(@RequestParam List<String> groups) {
        var data = codeBook.getMulti(groups);
        Map<String, List<Map<String, Object>>> res = new LinkedHashMap<>();

        data.forEach((g, list) -> {
            List<Map<String, Object>> rows = new ArrayList<>(list.size());
            for (CommonCode cc : list) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("code", cc.getCode());
                m.put("name", cc.getCodeName());
                m.put("sortOrder", cc.getSortOrder());
                rows.add(m);
            }
            res.put(g, rows);
        });

        return res;
    }
}
