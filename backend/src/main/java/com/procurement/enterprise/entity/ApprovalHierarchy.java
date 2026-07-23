package com.procurement.enterprise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA entity for the {@code approval_hierarchies} table.
 * <p>
 * Defines the ordered chain of approvers configured for a {@link Department}.
 * Example: Department (level 1: Manager) &rarr; (level 2: Finance) &rarr; (level 3: Procurement Head).
 * The {@link com.procurement.enterprise.service.RequestRoutingService} walks this chain,
 * lowest level first, to automatically route a newly created purchase request.
 */
@Entity
@Table(name = "approval_hierarchies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHierarchy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Department this hierarchy level belongs to.
     * FK: {@code approval_hierarchies.department_id -> departments.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /**
     * Sequence position of this approver in the department's chain.
     * Level 1 is always the first approver assigned when a request is routed.
     */
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * The user designated as approver for this level of this department's hierarchy.
     * FK: {@code approval_hierarchies.approver_id -> users.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
