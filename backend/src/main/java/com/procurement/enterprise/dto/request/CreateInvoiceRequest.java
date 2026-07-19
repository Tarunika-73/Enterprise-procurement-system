package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.InvoiceStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateInvoiceRequest {
    @NotBlank(message = "Invoice number is required") @Size(max = 100, message = "Invoice number must not exceed 100 characters") private String invoiceNumber;
    @NotNull(message = "Receipt ID is required") private Long receiptId;
    @NotNull(message = "Vendor ID is required") private Long vendorId;
    @NotNull(message = "Invoice date is required") private LocalDate invoiceDate;
    @NotNull(message = "Due date is required") private LocalDate dueDate;
    @NotNull(message = "Total amount is required") @PositiveOrZero(message = "Total amount must be zero or positive") private BigDecimal totalAmount;
    private InvoiceStatus status;
}
