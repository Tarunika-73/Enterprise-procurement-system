package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.ApprovalActionTaken;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class ApprovalHistoryResponse {
    private Long id; private Long approvalId; private Long actionById; private String actionByName;
    private ApprovalActionTaken actionTaken; private String comments;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
