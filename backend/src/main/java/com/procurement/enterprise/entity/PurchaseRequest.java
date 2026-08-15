package com.procurement.enterprise.entity;

import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.enums.RequestPriority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code purchase_requests} table.
 * Represents a procurement request raised by an employee.
 */
@Entity
@Table(name = "purchase_requests")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true, length = 50)
    private String requestNumber;

    /**
     * Employee who raised this request.
     * FK: {@code purchase_requests.requester_id → users.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * Department the request belongs to.
     * FK: {@code purchase_requests.department_id → departments.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    @Builder.Default
    private RequestPriority priority = RequestPriority.NORMAL;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    /**
     * Workflow status. Maps SQL ENUM('Draft','Submitted','Pending','Approved','Rejected','Cancelled','Closed').
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PurchaseRequestStatus status = PurchaseRequestStatus.DRAFT;

    @Column(name = "total_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Assigned department manager for approval.
     * FK: {@code purchase_requests.manager_id → users.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    /**
     * Current manager / approver for this request.
     * FK: {@code purchase_requests.current_approver_id → users.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_approver_id")
    private User currentApprover;

    @Column(name = "manager_remarks", columnDefinition = "TEXT")
    private String managerRemarks;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return this.id; }
    public String getRequestNumber() { return this.requestNumber; }
    public Department getDepartment() { return this.department; }
    public com.procurement.enterprise.enums.PurchaseRequestStatus getStatus() { return this.status; }
    public void setStatus(com.procurement.enterprise.enums.PurchaseRequestStatus status) { this.status = status; }
    public java.math.BigDecimal getTotalAmount() { return this.totalAmount; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public LocalDateTime getUpdatedAt() { return this.updatedAt; }
}
