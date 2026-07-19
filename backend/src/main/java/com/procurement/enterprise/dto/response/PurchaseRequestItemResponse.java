package com.procurement.enterprise.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class PurchaseRequestItemResponse {
    private Long id; private Long purchaseRequestId; private Long productId; private String productName;
    private String productSku; private Integer quantity; private BigDecimal estimatedPrice;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
