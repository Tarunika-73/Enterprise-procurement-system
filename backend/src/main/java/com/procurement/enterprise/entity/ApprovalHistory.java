```java
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

    /*
     * Approval record connected to this history.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approval_id", nullable = false)
    private Approval approval;

    /*
     * User who approved or rejected the request.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_by_id", nullable = false)
    private User actionBy;

    /*
     * Action performed by the approver.
     * Example: APPROVED or REJECTED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken", nullable = false, length = 20)
    private ApprovalActionTaken actionTaken;

    /*
     * Approval level at which the action was performed.
     */
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    /*
     * Comments or reason entered by the approver.
     */
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    /*
     * Used for soft deletion if required by the project.
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /*
     * Date and time when this history record was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```
