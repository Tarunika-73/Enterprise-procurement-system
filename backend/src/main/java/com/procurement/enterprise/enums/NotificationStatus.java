package com.procurement.enterprise.enums;

/**
 * Represents the delivery and read status of a notification.
 */
public enum NotificationStatus {

    /** Notification has been created but not yet dispatched. */
    PENDING,

    /** Notification has been successfully sent to the user. */
    SENT,

    /** Notification dispatch failed due to a delivery error. */
    FAILED,

    /** Notification has been read by the recipient. */
    READ
}
