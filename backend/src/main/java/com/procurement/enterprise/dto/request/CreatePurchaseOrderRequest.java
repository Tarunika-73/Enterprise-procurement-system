package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePurchaseOrderRequest {
    @NotNull(message = "Purchase request ID is required") private Long purchaseRequestId;
    @NotNull(message = "Vendor ID is required") private Long vendorId;
    @NotNull(message = "Total amount is required") @PositiveOrZero(message = "Total amount must be zero or positive") private BigDecimal totalAmount;
    private LocalDate expectedDeliveryDate;
}
