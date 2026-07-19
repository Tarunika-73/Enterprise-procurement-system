package com.procurement.enterprise.enums;

/**
 * Represents the lifecycle status of a user account.
 */
public enum UserStatus {

    /** Account is active and the user can log in. */
    ACTIVE,

    /** Account has been deactivated by an administrator. */
    INACTIVE,

    /** Account is temporarily locked due to failed login attempts. */
    LOCKED,

    /** Account has been suspended pending investigation or review. */
    SUSPENDED
}
