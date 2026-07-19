package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateSupplierPerformanceRequest {
    @Min(value = 1, message = "Quality rating must be at least 1") @Max(value = 5, message = "Quality rating must not exceed 5") private Integer qualityRating;
    @Min(value = 1, message = "Delivery rating must be at least 1") @Max(value = 5, message = "Delivery rating must not exceed 5") private Integer deliveryRating;
    @Min(value = 1, message = "Pricing rating must be at least 1") @Max(value = 5, message = "Pricing rating must not exceed 5") private Integer pricingRating;
    private String comments;
}
