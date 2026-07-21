package com.procurement.enterprise.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procurement.enterprise.dto.request.ApprovalActionRequest;
import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.enums.ApprovalStatus;

/**
 * Service contract for approval hierarchy management.
 * Handles the multi-level approval workflow for purchase requests,
 * including creation of approval steps, actioning them (approve / reject /
 * escalate) and querying the resulting approval history.
 */
public interface ApprovalService {

    ApprovalResponse createApproval(CreateApprovalRequest request);

    ApprovalResponse updateApproval(Long id, UpdateApprovalRequest request);

    void deleteApproval(Long id);

    ApprovalResponse getApprovalById(Long id);

    Page<ApprovalResponse> getAllApprovals(Pageable pageable);

    Page<ApprovalResponse> getApprovalsByPurchaseRequest(Long purchaseRequestId, Pageable pageable);

    Page<ApprovalResponse> getApprovalsByApprover(Long approverId, Pageable pageable);

    Page<ApprovalResponse> getApprovalsByStatus(ApprovalStatus status, Pageable pageable);

    /**
     * Records a decision (approve / reject / escalate) against a pending
     * approval step and appends an entry to the approval history.
     */
    ApprovalResponse takeAction(Long id, ApprovalActionRequest request);

    Page<ApprovalHistoryResponse> getApprovalHistory(Long approvalId, Pageable pageable);
}
