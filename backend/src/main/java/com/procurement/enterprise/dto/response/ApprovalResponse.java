package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.ApprovalStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class ApprovalResponse {
    private Long id; private Long purchaseRequestId; private Integer level; private Long approverId;
    private String approverName; private ApprovalStatus status; private String comments;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
