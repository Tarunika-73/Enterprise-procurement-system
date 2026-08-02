package com.procurement.enterprise.enums;

/**
 * Maps the SQL ENUM for {@code purchase_requests.status}.
 * Values: 'Draft', 'Submitted', 'Pending', 'Approved', 'Rejected', 'Cancelled', 'Closed'
 */
public enum PurchaseRequestStatus {
    DRAFT,
    SUBMITTED,
    PENDING,
    APPROVED,
    REJECTED,
    RETURNED_FOR_MODIFICATION,
    CANCELLED,
    CLOSED
}
