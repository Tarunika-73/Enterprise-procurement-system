package com.procurement.enterprise.dto.approvalhistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistoryResponse {

    private Long historyId;

    private Long requestId;

    private Long approverId;

    private Integer approvalLevel;

    private String actionTaken;

    private String remarks;

    private LocalDateTime actionTime;
}