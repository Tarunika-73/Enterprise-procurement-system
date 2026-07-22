package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupplierPerformanceResponse {

    private Long id;

    private Long vendorId;

    private String vendorName;

    private Long purchaseOrderId;

    private String purchaseOrderNumber;

    private Integer qualityRating;

    private Integer deliveryRating;

    private Integer pricingRating;

    private String comments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}