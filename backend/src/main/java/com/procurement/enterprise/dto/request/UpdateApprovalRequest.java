package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.ApprovalStatus;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateApprovalRequest {
    @Positive(message = "Approval level must be positive") private Integer level;
    private Long approverId; private ApprovalStatus status; private String comments;
}
