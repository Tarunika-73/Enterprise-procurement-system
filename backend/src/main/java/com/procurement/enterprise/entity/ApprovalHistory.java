package com.procurement.enterprise.entity;

import com.procurement.enterprise.enums.ApprovalActionTaken;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "approval_history",
        indexes = {
                @Index(
                        name = "idx_approval_history_approval",
                        columnList = "approval_id"
                ),
                @Index(
                        name = "idx_approval_history_action_by",
                        columnList = "action_by_id"
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Approval record associated with this history.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id")
    private Approval approval;

    /** The active Manager workflow operates on purchase_requests, not legacy requisitions. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    /**
     * User who performed the action.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_by_id", nullable = false)
    private User actionBy;

    /**
     * APPROVED / REJECTED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken", nullable = false)
    private ApprovalActionTaken actionTaken;

    /**
     * Approval level.
     */
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    /**
     * Remarks entered by approver.
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
