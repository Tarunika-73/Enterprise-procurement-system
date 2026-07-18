package com.procurement.enterprise.enums;

/**
 * Represents the status of a vendor estimate submitted against a purchase request.
 *
 * <p>Lifecycle flow:
 * <pre>
 *   PENDING → SUBMITTED → ACCEPTED
 *                       → REJECTED
 * </pre>
 */
public enum VendorEstimateStatus {

    /** Estimate has been requested from the vendor but not yet submitted. */
    PENDING,

    /** Vendor has submitted the estimate for review. */
    SUBMITTED,

    /** Estimate has been accepted and selected for purchase order creation. */
    ACCEPTED,

    /** Estimate has been rejected in favour of another vendor's offer. */
    REJECTED
}
