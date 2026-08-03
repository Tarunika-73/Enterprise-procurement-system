package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovePaymentRequest {

    @NotBlank(message = "Payment method is required.")
    @Size(max = 50, message = "Payment method must not exceed 50 characters.")
    private String paymentMethod;

    @Size(max = 500, message = "Remarks must not exceed 500 characters.")
    private String remarks;
}
