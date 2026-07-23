package com.procurement.enterprise.entity;
import com.procurement.enterprise.enums.RequisitionPriority;
import com.procurement.enterprise.enums.RequisitionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_requisitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestNumber;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequisitionPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequisitionStatus status;

    private Long currentApproverId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}