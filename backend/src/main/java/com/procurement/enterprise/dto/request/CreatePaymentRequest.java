package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePaymentRequest {
    @NotNull(message = "Invoice ID is required") private Long invoiceId;
    @NotBlank(message = "Payment reference is required") @Size(max = 100, message = "Payment reference must not exceed 100 characters") private String paymentReference;
    @NotNull(message = "Amount paid is required") @Positive(message = "Amount paid must be positive") private BigDecimal amountPaid;
    @NotNull(message = "Payment date is required") private LocalDate paymentDate;
    @NotBlank(message = "Payment method is required") @Size(max = 50, message = "Payment method must not exceed 50 characters") private String paymentMethod;
    private PaymentStatus status;
}
