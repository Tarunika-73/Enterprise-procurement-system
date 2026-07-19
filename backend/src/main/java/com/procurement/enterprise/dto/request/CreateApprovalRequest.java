package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.ApprovalStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateApprovalRequest {
    @NotNull(message = "Purchase request ID is required") private Long purchaseRequestId;
    @NotNull(message = "Approval level is required") @Positive(message = "Approval level must be positive") private Integer level;
    private Long approverId; private ApprovalStatus status; private String comments;
}
