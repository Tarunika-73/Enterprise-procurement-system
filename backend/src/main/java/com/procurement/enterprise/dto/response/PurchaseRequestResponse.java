package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PurchaseRequestStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class PurchaseRequestResponse {
    private Long id; private String requestNumber; private Long requesterId; private String requesterName;
    private Long departmentId; private String departmentName; private String justification;
    private PurchaseRequestStatus status; private BigDecimal totalAmount;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
