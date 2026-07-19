package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateVendorEstimateRequest {
    @Size(max = 255, message = "Estimate document URL must not exceed 255 characters") private String estimateDocumentUrl;
    @PositiveOrZero(message = "Estimated total must be zero or positive") private BigDecimal estimatedTotal;
    private LocalDate validUntil; private Boolean isSelected;
}
