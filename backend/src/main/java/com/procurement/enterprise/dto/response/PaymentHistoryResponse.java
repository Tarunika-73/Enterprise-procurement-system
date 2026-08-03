package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentHistoryResponse {

    private Long id;
    private String paymentReference;
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private String requestNumber;
    private Long vendorId;
    private String vendorName;
    private String invoiceNumber;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String remarks;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
