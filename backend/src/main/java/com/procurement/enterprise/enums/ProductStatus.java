package com.procurement.enterprise.enums;

/**
 * Represents the lifecycle status of a product in the procurement catalog.
 */
public enum ProductStatus {

    /** Product is available and can be added to purchase requests. */
    ACTIVE,

    /** Product has been deactivated and is not available for procurement. */
    INACTIVE,

    /** Product is temporarily unavailable due to stock depletion. */
    OUT_OF_STOCK,

    /** Product has been permanently discontinued and will not be restocked. */
    DISCONTINUED
}
