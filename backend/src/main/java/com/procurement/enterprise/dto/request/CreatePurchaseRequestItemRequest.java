package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePurchaseRequestItemRequest {
    @NotNull(message = "Purchase request ID is required") private Long purchaseRequestId;
    @NotNull(message = "Product ID is required") private Long productId;
    @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be positive") private Integer quantity;
    @NotNull(message = "Estimated price is required") @PositiveOrZero(message = "Estimated price must be zero or positive") private BigDecimal estimatedPrice;
}
