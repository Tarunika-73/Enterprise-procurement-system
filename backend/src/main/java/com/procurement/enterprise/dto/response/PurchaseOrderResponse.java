package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PurchaseOrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Getter
@Builder
public class PurchaseOrderResponse {
    private Long id;
    private String purchaseOrderNumber;
    private Long purchaseRequestId;
    private String requestNumber;
    private Long vendorId;
    private String vendorName;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
