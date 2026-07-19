package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePurchaseOrderItemRequest {
    @NotNull(message = "Purchase order ID is required") private Long purchaseOrderId;
    @NotNull(message = "Product ID is required") private Long productId;
    @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be positive") private Integer quantity;
    @NotNull(message = "Unit price is required") @PositiveOrZero(message = "Unit price must be zero or positive") private BigDecimal unitPrice;
    @NotNull(message = "Total price is required") @PositiveOrZero(message = "Total price must be zero or positive") private BigDecimal totalPrice;
}
