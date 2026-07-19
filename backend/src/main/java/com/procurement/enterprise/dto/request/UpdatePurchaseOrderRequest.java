package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.PurchaseOrderStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdatePurchaseOrderRequest {
    private Long vendorId; private PurchaseOrderStatus status;
    @PositiveOrZero(message = "Total amount must be zero or positive") private BigDecimal totalAmount;
    private LocalDate expectedDeliveryDate;
}
