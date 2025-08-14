// src/main/java/com/wino/wino_api/dto/common/CommonCodeDto.java
package com.wino.wino_api.dto.common;

import com.wino.wino_api.entity.common.CommonCode;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommonCodeDto {
    private Long id;
    private String code;
    private String codeName;
    private Integer sortOrder;
    private String useYn; // "Y"/"N"
    private String delYn; // "N"=표시, "Y"=숨김
    private Long parentId;

    public static CommonCodeDto of(CommonCode e) {
        return CommonCodeDto.builder()
                .id(e.getId())
                .code(e.getCode())
                .codeName(e.getCodeName())
                .sortOrder(e.getSortOrder())
                .useYn(e.getUseYn())
                .delYn(e.getDelYn())
                .parentId(e.getParent() != null ? e.getParent().getId() : null)
                .build();
    }
}
