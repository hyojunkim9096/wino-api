// src/main/java/com/wino/wino_api/service/common/CodeBookService.java
package com.wino.wino_api.service.common;

import com.wino.wino_api.entity.common.CommonCode;
import com.wino.wino_api.repository.common.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CodeBookService {

    private final CommonCodeRepository repo;

    @Cacheable(value="codebook", key="#groupCode")
    public List<CommonCode> getItems(String groupCode) {
        return repo.findByCodeAndParentIsNullAndDelYn(groupCode, "N")
                .map(g -> repo.findByParentIdAndDelYnOrderBySortOrderAscCodeAsc(g.getId(), "N")
                        .stream().filter(cc -> "Y".equals(cc.getUseYn())).toList())
                .orElse(List.of());
    }

    @Cacheable(value="codebookMulti", key="#groups")
    public Map<String, List<CommonCode>> getMulti(Collection<String> groups) {
        Map<String, List<CommonCode>> out = new LinkedHashMap<>();
        for (String g : groups) out.put(g, getItems(g));
        return out;
    }

    @Cacheable(value="codeLabel", key="#groupCode + '::' + #code")
    public String label(String groupCode, String code) {
        return getItems(groupCode).stream()
                .filter(i -> Objects.equals(i.getCode(), code))
                .map(CommonCode::getCodeName)
                .findFirst().orElse(code);
    }
}
