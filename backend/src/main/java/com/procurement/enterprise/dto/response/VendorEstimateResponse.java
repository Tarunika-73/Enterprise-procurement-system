package com.procurement.enterprise.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Getter @Builder
public class VendorEstimateResponse {
    private Long id; private Long purchaseRequestId; private String requestNumber; private Long vendorId;
    private String vendorName; private String estimateDocumentUrl; private BigDecimal estimatedTotal;
    private LocalDate validUntil; private Boolean isSelected; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
