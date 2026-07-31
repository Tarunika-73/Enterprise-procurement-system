package com.procurement.enterprise.service;

import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.PurchaseRequisition;

/**
 * Engine behind two of the approval module's core features:
 *
 *  1. Automatic Request Routing — when a purchase requisition is
 *     created, decide who the first approver is by matching the
 *     requisition's department + estimated amount against the
 *     configured {@link ApprovalHierarchyService} rules (falling back to
 *     the department manager when no rule matches).
 *
 *  2. Multi-Level Approval Workflow — when an approver approves their
 *     level, decide whether the request needs to move to another level
 *     (and who the next approver is) or is now fully approved, based on
 *     the same hierarchy configuration.
 */
public interface ApprovalRoutingService {

    /**
     * Routes a newly created purchase requisition to its first
     * approver, creating the corresponding {@link Approval} record.
     *
     * @return the created Approval, or {@code null} if no approver
     *         could be resolved (e.g. department has no manager and no
     *         hierarchy is configured) — the caller should surface this
     *         as "pending manual assignment".
     */
    Approval routeNewRequest(PurchaseRequisition requisition);

    /**
     * Advances a just-approved approval to the next configured level,
     * or marks the request fully approved if there is no next level.
     * Mutates and persists both the {@link Approval} and the parent
     * {@link PurchaseRequisition} status.
     *
     * @return the updated (saved) Approval
     */
    Approval progressAfterApproval(Approval approval);

    /**
     * Marks the parent purchase requisition of a rejected approval as
     * REJECTED, ending the workflow.
     */
    void markRequisitionRejected(PurchaseRequisition requisition);

    /**
     * Total number of levels configured for the hierarchy this approval
     * is routed through (1 for the legacy/no-hierarchy fallback).
     */
    int getTotalLevels(Approval approval);
}
