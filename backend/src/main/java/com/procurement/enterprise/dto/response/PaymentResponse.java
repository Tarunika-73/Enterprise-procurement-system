package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Getter @Builder
public class PaymentResponse {
    private Long id; private Long invoiceId; private String invoiceNumber; private String paymentReference;
    private BigDecimal amountPaid; private LocalDate paymentDate; private String paymentMethod; private PaymentStatus status;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
