package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateVendorEstimateRequest {
    @NotNull(message = "Purchase request ID is required") private Long purchaseRequestId;
    @NotNull(message = "Vendor ID is required") private Long vendorId;
    @Size(max = 255, message = "Estimate document URL must not exceed 255 characters") private String estimateDocumentUrl;
    @NotNull(message = "Estimated total is required") @PositiveOrZero(message = "Estimated total must be zero or positive") private BigDecimal estimatedTotal;
    private LocalDate validUntil;
}
