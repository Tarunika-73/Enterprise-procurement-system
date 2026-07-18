package com.procurement.enterprise.enums;

/**
 * Represents the compliance verification status of a vendor's submitted document.
 */
public enum SupplierComplianceStatus {

    /** Vendor has met all compliance requirements for this document. */
    COMPLIANT,

    /** Vendor has failed to meet one or more compliance requirements. */
    NON_COMPLIANT,

    /** Document is currently being reviewed by the compliance team. */
    UNDER_REVIEW
}
