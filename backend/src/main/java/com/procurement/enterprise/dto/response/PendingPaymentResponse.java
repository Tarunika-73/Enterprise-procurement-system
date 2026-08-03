package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PendingPaymentResponse {

    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private String requestNumber;
    private Long vendorId;
    private String vendorName;
    private String vendorEmail;
    private String departmentName;
    private BigDecimal totalAmount;
    private LocalDate expectedDeliveryDate;
    private LocalDate deliveryDate;
    private String invoiceNumber;
    private LocalDateTime createdAt;
}
