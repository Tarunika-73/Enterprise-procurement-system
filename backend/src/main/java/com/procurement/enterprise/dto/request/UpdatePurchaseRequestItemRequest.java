package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdatePurchaseRequestItemRequest {
    private Long productId;
    @Positive(message = "Quantity must be positive") private Integer quantity;
    @PositiveOrZero(message = "Estimated price must be zero or positive") private BigDecimal estimatedPrice;
}
