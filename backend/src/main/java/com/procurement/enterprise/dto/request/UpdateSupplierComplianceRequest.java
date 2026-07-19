package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateSupplierComplianceRequest {
    @Size(max = 100, message = "Document type must not exceed 100 characters") private String documentType;
    @Size(max = 255, message = "Document URL must not exceed 255 characters") private String documentUrl;
    private LocalDate expiryDate;
    @Size(max = 50, message = "Status must not exceed 50 characters") private String status;
}
