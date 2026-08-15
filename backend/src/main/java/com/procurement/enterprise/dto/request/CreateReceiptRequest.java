package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateReceiptRequest {
    @NotNull(message = "Delivery ID is required") private Long deliveryId;
    @NotNull(message = "Receipt date is required") private LocalDate receiptDate;
    private String conditionNotes;
}
