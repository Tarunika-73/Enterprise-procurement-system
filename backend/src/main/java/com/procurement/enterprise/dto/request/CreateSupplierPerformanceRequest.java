package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSupplierPerformanceRequest {

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @NotNull(message = "Purchase order ID is required")
    private Long purchaseOrderId;

    @NotNull(message = "Quality rating is required")
    @Min(value = 1, message = "Quality rating must be at least 1")
    @Max(value = 5, message = "Quality rating must not exceed 5")
    private Integer qualityRating;

    @NotNull(message = "Delivery rating is required")
    @Min(value = 1, message = "Delivery rating must be at least 1")
    @Max(value = 5, message = "Delivery rating must not exceed 5")
    private Integer deliveryRating;

    @NotNull(message = "Pricing rating is required")
    @Min(value = 1, message = "Pricing rating must be at least 1")
    @Max(value = 5, message = "Pricing rating must not exceed 5")
    private Integer pricingRating;

    private String comments;
}