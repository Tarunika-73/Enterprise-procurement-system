package com.procurement.enterprise.enums;

/**
 * Represents the status of a goods receipt created after delivery.
 *
 * <p>Lifecycle flow:
 * <pre>
 *   PENDING → RECEIVED
 *           → REJECTED
 * </pre>
 */
public enum ReceiptStatus {

    /** Receipt has been created but goods inspection is not yet complete. */
    PENDING,

    /** Goods have been inspected and accepted into inventory. */
    RECEIVED,

    /** Goods were inspected and rejected due to quality or quantity issues. */
    REJECTED
}
