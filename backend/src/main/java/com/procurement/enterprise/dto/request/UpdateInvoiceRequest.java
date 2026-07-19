package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.InvoiceStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateInvoiceRequest {
    private LocalDate invoiceDate; private LocalDate dueDate;
    @PositiveOrZero(message = "Total amount must be zero or positive") private BigDecimal totalAmount;
    private InvoiceStatus status;
}
