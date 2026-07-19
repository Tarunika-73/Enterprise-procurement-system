package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdatePaymentRequest {
    @Positive(message = "Amount paid must be positive") private BigDecimal amountPaid;
    private LocalDate paymentDate;
    @Size(max = 50, message = "Payment method must not exceed 50 characters") private String paymentMethod;
    private PaymentStatus status;
}
