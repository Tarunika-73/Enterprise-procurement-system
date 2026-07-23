package com.procurement.enterprise.dto.purchaserequisition;

import com.procurement.enterprise.enums.RequisitionPriority;
import com.procurement.enterprise.enums.RequisitionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseRequisitionResponse {

    private Long id;

    private String requestNumber;

    private Long employeeId;

    private Long departmentId;

    private Long categoryId;

    private String description;

    private Integer quantity;

    private BigDecimal estimatedAmount;

    private RequisitionPriority priority;

    private RequisitionStatus status;

    private Long currentApproverId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}