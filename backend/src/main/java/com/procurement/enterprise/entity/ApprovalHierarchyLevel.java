package com.procurement.enterprise.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * One level of an {@link ApprovalHierarchy}. Each level names the
 * {@link Role} that must approve at that level before the request can
 * move on to the next level (or be marked fully approved when it is the
 * last level).
 */
@Entity
@Table(
        name = "approval_hierarchy_levels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_hierarchy_level",
                columnNames = {"approval_hierarchy_id", "level_number"}
        )
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApprovalHierarchyLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_hierarchy_id", nullable = false)
    private ApprovalHierarchy approvalHierarchy;

    /**
     * 1-based position of this level within its hierarchy.
     */
    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    /**
     * Role that must hold the approver seat at this level
     * (e.g. Department Manager, Procurement Officer, Finance Officer, Admin).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_role_id", nullable = false)
    private Role approverRole;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
