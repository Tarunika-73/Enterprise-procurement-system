package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.*;

/** Request DTO for updating an existing approval-hierarchy level. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApprovalHierarchyRequest {

    @Positive(message = "Level must be positive")
    private Integer level;

    private Long approverId;
}
