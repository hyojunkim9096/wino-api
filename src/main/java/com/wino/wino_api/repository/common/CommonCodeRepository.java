// src/main/java/com/wino/wino_api/repository/common/CommonCodeRepository.java
package com.wino.wino_api.repository.common;

import com.wino.wino_api.entity.common.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {
    List<CommonCode> findByParentIsNullAndDelYnOrderBySortOrderAscCodeAsc(String delYn);
    List<CommonCode> findByParentIdAndDelYnOrderBySortOrderAscCodeAsc(Long parentId, String delYn);

    boolean existsByParentIsNullAndCode(String code);
    boolean existsByParentIdAndCode(Long parentId, String code);

    Optional<CommonCode> findByCodeAndParentIsNullAndDelYn(String code, String delYn);
}
