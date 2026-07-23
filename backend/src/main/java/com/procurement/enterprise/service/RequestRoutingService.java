package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;

import java.util.Optional;

/**
 * Handles automatic routing of purchase requests through their department's
 * {@link com.procurement.enterprise.entity.ApprovalHierarchy} chain.
 * <p>
 * Flow: Purchase Request Created -&gt; find hierarchy for the request's department
 * -&gt; assign the level-1 approver -&gt; set {@code currentApproverId} -&gt; status = PENDING.
 */
public interface RequestRoutingService {

    /**
     * Routes a newly created purchase request to the first approver in its
     * department's hierarchy. Sets the request's status to PENDING and populates
     * {@code currentApprover} / {@code currentLevel}, and records the level-1
     * entry in the Approval table.
     *
     * @param purchaseRequestId id of the purchase request to route
     * @return the updated purchase request
     */
    PurchaseRequestResponse routeNewRequest(Long purchaseRequestId);

    /**
     * Looks up the next hierarchy level (if any) after {@code currentLevel} for a department.
     * Used by the approval-workflow module to progress a request once its current
     * approver acts on it.
     */
    Optional<ApprovalHierarchyResponse> getNextLevel(Long departmentId, Integer currentLevel);

    /** Returns the first (level 1) hierarchy entry configured for a department, if any. */
    Optional<ApprovalHierarchyResponse> getFirstLevel(Long departmentId);
}
