package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.RequestPriority;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePurchaseRequestRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @NotBlank(message = "Request title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Business justification is required")
    @Size(min = 20, message = "Justification must be at least 20 characters")
    private String justification;

    @NotNull(message = "Expected delivery date is required")
    @FutureOrPresent(message = "Expected delivery date must be today or a future date")
    private LocalDate expectedDeliveryDate;

    @NotNull(message = "Priority is required")
    private RequestPriority priority;
}
