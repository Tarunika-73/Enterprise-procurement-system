package com.procurement.enterprise.dto.approvalworkflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Approver ID is required")
    private Long approverId;

    @NotBlank(message = "Remarks are required")
    private String remarks;
}