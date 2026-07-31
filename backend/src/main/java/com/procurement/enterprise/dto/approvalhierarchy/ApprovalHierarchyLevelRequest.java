package com.procurement.enterprise.dto.approvalhierarchy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHierarchyLevelRequest {

    @NotNull(message = "Level number is required")
    @Min(value = 1, message = "Level number must start at 1")
    private Integer levelNumber;

    @NotNull(message = "Approver role is required")
    private Long approverRoleId;
}
