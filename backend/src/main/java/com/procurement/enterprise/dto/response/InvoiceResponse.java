package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.InvoiceStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Getter @Builder
public class InvoiceResponse {
    private Long id; private String invoiceNumber; private Long receiptId; private Long vendorId; private String vendorName;
    private LocalDate invoiceDate; private LocalDate dueDate; private BigDecimal totalAmount; private InvoiceStatus status;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
