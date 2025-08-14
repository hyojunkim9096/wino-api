// src/main/java/com/wino/wino_api/entity/common/CommonCode.java
package com.wino.wino_api.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공통코드
 * - parent == null 이면 최상위(그룹)
 * - 하위(children)는 화면/API에 직접 노출하지 않도록 @JsonIgnore
 * - Builder 사용 시 기본값 유지하려면 @Builder.Default 필수
 */
@Entity
@Table(
        name = "common_code",
        indexes = {
                @Index(name = "ix_common_code_parent", columnList = "parent_id"),
                @Index(name = "ix_common_code_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"parent", "children"})
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommonCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 코드값 (그룹/하위 공통), 예: WORK_LOCATION / NARU 등 */
    @Column(length = 50, nullable = false)
    private String code;

    /** 표시명 */
    @Column(name = "code_name", length = 100, nullable = false)
    private String codeName;

    /** 상위 코드, null이면 최상위 그룹 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore // JSON 순환 방지 (DTO로 응답하므로 엔티티 직렬화 시 보호막)
    private CommonCode parent;

    /** 사용 여부 (Y/N) */
    @Builder.Default
    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn = "Y";

    /** 표시 여부 (del_yn = N 이면 화면 표시) */
    @Builder.Default
    @Column(name = "del_yn", length = 1, nullable = false)
    private String delYn = "N";

    /** 정렬 순서 */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 하위 코드 목록 (지연로딩), API 직렬화 방지 */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, code ASC")
    @JsonIgnore
    @Builder.Default
    private List<CommonCode> children = new ArrayList<>();

    /* 편의 메서드 (필요 시 사용)
    public void addChild(CommonCode child) {
        children.add(child);
        child.setParent(this);
    }
    */
}
