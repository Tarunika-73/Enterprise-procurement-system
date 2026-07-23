package com.procurement.enterprise.dto.purchaserequisition;

import com.procurement.enterprise.enums.RequisitionPriority;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseRequisitionRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long departmentId;

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal estimatedAmount;

    @NotNull
    private RequisitionPriority priority;
}