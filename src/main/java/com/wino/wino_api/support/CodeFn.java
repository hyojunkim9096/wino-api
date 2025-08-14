// src/main/java/com/wino/wino_api/support/CodeFn.java
package com.wino.wino_api.support;

import com.wino.wino_api.service.common.CodeBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("code")
@RequiredArgsConstructor
public class CodeFn {
    private final CodeBookService codeBook;
    public String label(String group, String code) { return codeBook.label(group, code); }
}
