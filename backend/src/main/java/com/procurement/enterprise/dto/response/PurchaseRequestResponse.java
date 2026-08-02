package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.enums.RequestPriority;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseRequestResponse {
    private Long id;
    private String requestNumber;
    private String title;
    private Long requesterId;
    private String requesterName;
    private String employeeCode;
    private Long departmentId;
    private String departmentName;
    private String justification;
    private RequestPriority priority;
    private LocalDate expectedDeliveryDate;
    private PurchaseRequestStatus status;
    private PurchaseRequestStatus approvalStatus;
    private BigDecimal totalAmount;
    private Long managerId;
    private String managerName;
    private Long currentApproverId;
    private String currentApproverName;
    private String managerRemarks;
    private LocalDateTime approvalDate;
    private Long productId;
    private String productName;
    private String productSku;
    private String categoryName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private List<PurchaseRequestItemResponse> items;
    private List<RequestTimelineEntry> timeline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
