package com.procurement.enterprise.dto.response;

import lombok.*;
import java.time.*;

@Getter @Builder
public class SupplierComplianceResponse {
    private Long id; private Long vendorId; private String vendorName; private String documentType;
    private String documentUrl; private LocalDate expiryDate; private String status;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
