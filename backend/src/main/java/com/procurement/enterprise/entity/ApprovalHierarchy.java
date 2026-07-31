package com.procurement.enterprise.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a configurable approval hierarchy (rule set) that automatic
 * request routing and the multi-level approval workflow rely on.
 *
 * A hierarchy applies to a {@link Department} (or, when {@code department}
 * is null, to every department as an organisation-wide default) and to a
 * range of requisition amounts [{@code minAmount}, {@code maxAmount}].
 * When a purchase requisition is created, the routing engine picks the
 * best-matching hierarchy for that department + amount and creates the
 * chain of {@link ApprovalHierarchyLevel} approvers to route the request
 * through.
 */
@Entity
@Table(name = "approval_hierarchies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApprovalHierarchy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * Department this hierarchy applies to.
     * Null means the hierarchy is a global/default rule applied when no
     * department-specific hierarchy matches.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * Inclusive lower bound of the requisition estimated amount this
     * hierarchy applies to.
     */
    @Column(name = "min_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal minAmount;

    /**
     * Inclusive upper bound of the requisition estimated amount this
     * hierarchy applies to. Null means "no upper limit".
     */
    @Column(name = "max_amount", precision = 12, scale = 2)
    private BigDecimal maxAmount;

    /**
     * When multiple hierarchies could match the same request, the one
     * with the lowest priority value wins (0 = highest priority).
     */
    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "approvalHierarchy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApprovalHierarchyLevel> levels = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
