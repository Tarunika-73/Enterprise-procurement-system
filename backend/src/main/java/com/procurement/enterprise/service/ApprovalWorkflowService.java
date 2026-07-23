package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.approvalworkflow.ApprovalRequest;
import com.procurement.enterprise.dto.approvalworkflow.PendingApprovalResponse;
import com.procurement.enterprise.dto.approvalworkflow.RejectionRequest;
import com.procurement.enterprise.dto.approvalworkflow.WorkflowStatusResponse;

import java.util.List;

/**
 * Service interface for approval workflow operations.
 */
public interface ApprovalWorkflowService {

    /**
     * Approves a purchase requisition.
     *
     * @param request approval request details
     * @return updated workflow status
     */
    WorkflowStatusResponse approveRequest(ApprovalRequest request);

    /**
     * Rejects a purchase requisition.
     *
     * @param request rejection request details
     * @return updated workflow status
     */
    WorkflowStatusResponse rejectRequest(RejectionRequest request);

    /**
     * Gets all requests pending for a particular approver.
     *
     * @param approverId approver user ID
     * @return pending approval requests
     */
    List<PendingApprovalResponse> getPendingRequests(Long approverId);

    /**
     * Gets all requests assigned to or handled by an approver.
     *
     * @param approverId approver user ID
     * @return requests associated with the approver
     */
    List<PendingApprovalResponse> getRequestsByApprover(Long approverId);

    /**
     * Gets the current approval workflow status of a request.
     *
     * @param requestId purchase requisition ID
     * @return current workflow status
     */
    WorkflowStatusResponse getWorkflowStatus(Long requestId);
}