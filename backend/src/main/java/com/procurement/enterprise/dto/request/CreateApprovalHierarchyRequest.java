package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/** Request DTO for configuring a new approval-hierarchy level for a department. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApprovalHierarchyRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Level is required")
    @Positive(message = "Level must be positive")
    private Integer level;

    @NotNull(message = "Approver ID is required")
    private Long approverId;
}
