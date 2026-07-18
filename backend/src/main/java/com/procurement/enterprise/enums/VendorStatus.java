package com.procurement.enterprise.enums;

/**
 * Represents the operational status of a vendor in the procurement system.
 */
public enum VendorStatus {

    /** Vendor is approved and eligible to receive purchase orders. */
    ACTIVE,

    /** Vendor has been deactivated and cannot receive new orders. */
    INACTIVE,

    /** Vendor has been blacklisted and is prohibited from all procurement activities. */
    BLACKLISTED,

    /** Vendor registration is submitted and awaiting administrator approval. */
    PENDING_APPROVAL
}
