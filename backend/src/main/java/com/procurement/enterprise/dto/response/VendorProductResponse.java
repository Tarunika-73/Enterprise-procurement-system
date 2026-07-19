package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class VendorProductResponse {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private Long productId;
    private String productName;
    private String productSku;
    private BigDecimal price;
    private Integer leadTimeDays;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
