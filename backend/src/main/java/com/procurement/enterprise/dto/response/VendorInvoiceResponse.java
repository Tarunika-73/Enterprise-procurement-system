package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class VendorInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
}
