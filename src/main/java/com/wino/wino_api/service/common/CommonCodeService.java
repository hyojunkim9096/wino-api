// src/main/java/com/wino/wino_api/service/common/CommonCodeService.java
package com.wino.wino_api.service.common;

import com.wino.wino_api.dto.common.CommonCodeDto;
import com.wino.wino_api.entity.common.CommonCode;
import com.wino.wino_api.repository.common.CommonCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeRepository repo;

    @Transactional(readOnly = true)
    public List<CommonCode> getTopGroups() {
        return repo.findByParentIsNullAndDelYnOrderBySortOrderAscCodeAsc("N");
    }

    @Transactional(readOnly = true)
    public List<CommonCode> getChildren(Long parentId) {
        return repo.findByParentIdAndDelYnOrderBySortOrderAscCodeAsc(parentId, "N");
    }

    @Transactional
    @CacheEvict(value = {"codebook","codebookMulti","codeLabel"}, allEntries = true)
    public CommonCode create(CommonCodeDto dto) {
        CommonCode parent = null;
        if (dto.getParentId() != null) {
            parent = repo.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("parent not found"));
            if (repo.existsByParentIdAndCode(parent.getId(), dto.getCode()))
                throw new IllegalArgumentException("duplicate code under same parent");
        } else {
            if (repo.existsByParentIsNullAndCode(dto.getCode()))
                throw new IllegalArgumentException("duplicate top-level code");
        }

        CommonCode cc = CommonCode.builder()
                .code(dto.getCode().trim())
                .codeName(dto.getCodeName().trim())
                .useYn(dto.getUseYn() == null ? "Y" : dto.getUseYn())
                .delYn(dto.getDelYn() == null ? "N" : dto.getDelYn())
                .sortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder())
                .parent(parent)
                .build();
        return repo.save(cc);
    }

    @Transactional
    @CacheEvict(value = {"codebook","codebookMulti","codeLabel"}, allEntries = true)
    public CommonCode update(Long id, CommonCodeDto dto) {
        CommonCode cc = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("code not found"));
        if (dto.getCode() != null) cc.setCode(dto.getCode().trim());
        if (dto.getCodeName() != null) cc.setCodeName(dto.getCodeName().trim());
        if (dto.getUseYn() != null) cc.setUseYn(dto.getUseYn());
        if (dto.getDelYn() != null) cc.setDelYn(dto.getDelYn());
        if (dto.getSortOrder() != null) cc.setSortOrder(dto.getSortOrder());
        return cc;
    }

    @Transactional
    @CacheEvict(value = {"codebook","codebookMulti","codeLabel"}, allEntries = true)
    public void softDelete(Long id) {
        CommonCode cc = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("code not found"));
        cc.setDelYn("Y");
    }
}
