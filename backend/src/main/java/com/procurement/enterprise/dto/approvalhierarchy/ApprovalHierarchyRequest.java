package com.procurement.enterprise.dto.approvalhierarchy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHierarchyRequest {

    @NotBlank(message = "Hierarchy name is required")
    private String name;

    /**
     * Department this hierarchy applies to. Leave null to define a
     * global/default hierarchy used when no department-specific rule
     * matches.
     */
    private Long departmentId;

    @NotNull(message = "Minimum amount is required")
    private BigDecimal minAmount;

    /**
     * Leave null for "no upper limit".
     */
    private BigDecimal maxAmount;

    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Boolean isActive = true;

    @NotEmpty(message = "At least one approval level is required")
    @Valid
    private List<ApprovalHierarchyLevelRequest> levels;
}
