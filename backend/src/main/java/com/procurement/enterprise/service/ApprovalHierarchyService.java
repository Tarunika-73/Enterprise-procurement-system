package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyRequest;
import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing configurable, multi-level approval hierarchies.
 *
 * This is the "Approval Hierarchy Management" module: it lets an admin
 * define, per department and requisition amount range, the ordered chain
 * of approver roles a purchase requisition must pass through. The
 * {@link ApprovalRoutingService} reads this configuration to perform
 * automatic request routing and to drive the multi-level approval
 * workflow.
 */
public interface ApprovalHierarchyService {

    ApprovalHierarchyResponse createHierarchy(ApprovalHierarchyRequest request);

    ApprovalHierarchyResponse updateHierarchy(Long id, ApprovalHierarchyRequest request);

    ApprovalHierarchyResponse getHierarchyById(Long id);

    Page<ApprovalHierarchyResponse> getAllHierarchies(Pageable pageable);

    void deleteHierarchy(Long id);

    /**
     * Resolves and returns the hierarchy that a purchase requisition for
     * the given department and estimated amount should be routed
     * through, so callers (e.g. the requisition team) can preview the
     * approval chain before submitting.
     */
    ApprovalHierarchyResponse resolveApplicableHierarchy(Long departmentId, java.math.BigDecimal amount);
}
