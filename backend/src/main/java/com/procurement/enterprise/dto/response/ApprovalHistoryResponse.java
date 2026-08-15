package com.procurement.enterprise.dto.response;

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
    private String requestNumber;
    private String employeeName;

    private Long approverId;

    private Integer approvalLevel;

    private String actionTaken;

    private String remarks;

    private LocalDateTime actionTime;
}
