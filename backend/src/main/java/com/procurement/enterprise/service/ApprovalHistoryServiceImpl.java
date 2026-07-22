```java
        package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.approvalhistory.ApprovalHistoryResponse;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for approval-history operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService {

    private final ApprovalHistoryRepository approvalHistoryRepository;

    /**
     * Returns the complete approval history of a purchase request.
     *
     * @param requestId purchase requisition ID
     * @return approval-history response list
     */
    @Override
    public List<ApprovalHistoryResponse> getApprovalHistory(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID must not be null."
            );
        }

        /*
         * The repository method expects the approval ID.
         *
         * This assumes the Approval entity ID is the same value used
         * as requestId in the workflow APIs.
         */
        List<ApprovalHistory> historyList =
                approvalHistoryRepository
                        .findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(
                                requestId
                        );

        if (historyList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No approval history found for request ID: " + requestId
            );
        }

        return historyList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns all approval-history records for a specific approver.
     *
     * @param approverId approver user ID
     * @return approval-history response list
     */
    @Override
    public List<ApprovalHistoryResponse> getHistoryByApprover(
            Long approverId
    ) {

        if (approverId == null) {
            throw new IllegalArgumentException(
                    "Approver ID must not be null."
            );
        }

        List<ApprovalHistory> historyList =
                approvalHistoryRepository
                        .findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(
                                approverId
                        );

        if (historyList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No approval history found for approver ID: "
                            + approverId
            );
        }

        return historyList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Converts ApprovalHistory entity into ApprovalHistoryResponse DTO.
     */
    private ApprovalHistoryResponse mapToResponse(
            ApprovalHistory history
    ) {

        Long requestId = null;
        Long approverId = null;
        String actionTaken = null;

        if (history.getApproval() != null) {

            /*
             * Change getRequestId() only when your Approval entity uses
             * another field name for the purchase requisition ID.
             */
            requestId = history.getApproval().getRequestId();
        }

        if (history.getActionBy() != null) {
            approverId = history.getActionBy().getId();
        }

        if (history.getActionTaken() != null) {
            actionTaken = history.getActionTaken().name();
        }

        return ApprovalHistoryResponse.builder()
                .historyId(history.getId())
                .requestId(requestId)
                .approverId(approverId)
                .approvalLevel(history.getApprovalLevel())
                .actionTaken(actionTaken)
                .remarks(history.getRemarks())
                .actionTime(history.getCreatedAt())
                .build();
    }
}
```
