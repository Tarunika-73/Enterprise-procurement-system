package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;

import java.util.List;

/**
 * Service interface for approval-history operations.
 */
public interface ApprovalHistoryService {

    /**
     * Gets the complete approval history for a purchase request.
     *
     * @param requestId purchase requisition ID
     * @return approval-history records
     */
    List<ApprovalHistoryResponse> getApprovalHistory(Long requestId);

    /**
     * Gets all approval-history records created by an approver.
     *
     * @param approverId approver user ID
     * @return approval-history records handled by the approver
     */
    List<ApprovalHistoryResponse> getHistoryByApprover(Long approverId);

    List<ApprovalHistoryResponse> getMyHistory();
}
