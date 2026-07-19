package com.procurement.enterprise.util;

/**
 * Application-wide constants.
 * Centralises all magic strings and values used across the application.
 */
public final class Constants {

    private Constants() {}

    // ----------------------------------------------------------------
    // API Version
    // ----------------------------------------------------------------
    public static final String API_V1 = "/v1";

    // ----------------------------------------------------------------
    // Endpoint Paths
    // ----------------------------------------------------------------
    public static final String AUTH_ENDPOINT                  = API_V1 + "/auth";
    public static final String ROLES_ENDPOINT                 = API_V1 + "/roles";
    public static final String DEPARTMENTS_ENDPOINT           = API_V1 + "/departments";
    public static final String USERS_ENDPOINT                 = API_V1 + "/users";
    public static final String VENDORS_ENDPOINT               = API_V1 + "/vendors";
    public static final String CATEGORIES_ENDPOINT            = API_V1 + "/categories";
    public static final String PRODUCTS_ENDPOINT              = API_V1 + "/products";
    public static final String VENDOR_PRODUCTS_ENDPOINT       = API_V1 + "/vendor-products";
    public static final String PURCHASE_REQUESTS_ENDPOINT     = API_V1 + "/purchase-requests";
    public static final String PURCHASE_REQUEST_ITEMS_ENDPOINT = API_V1 + "/purchase-request-items";
    public static final String APPROVALS_ENDPOINT             = API_V1 + "/approvals";
    public static final String APPROVAL_HISTORY_ENDPOINT      = API_V1 + "/approval-history";
    public static final String VENDOR_ESTIMATES_ENDPOINT      = API_V1 + "/vendor-estimates";
    public static final String PURCHASE_ORDERS_ENDPOINT       = API_V1 + "/purchase-orders";
    public static final String PURCHASE_ORDER_ITEMS_ENDPOINT  = API_V1 + "/purchase-order-items";
    public static final String DELIVERIES_ENDPOINT            = API_V1 + "/deliveries";
    public static final String RECEIPTS_ENDPOINT              = API_V1 + "/receipts";
    public static final String INVOICES_ENDPOINT              = API_V1 + "/invoices";
    public static final String PAYMENTS_ENDPOINT              = API_V1 + "/payments";
    public static final String NOTIFICATIONS_ENDPOINT         = API_V1 + "/notifications";
    public static final String AUDIT_LOGS_ENDPOINT            = API_V1 + "/audit-logs";
    public static final String SUPPLIER_PERFORMANCE_ENDPOINT  = API_V1 + "/supplier-performance";
    public static final String SUPPLIER_COMPLIANCE_ENDPOINT   = API_V1 + "/supplier-compliance";
    public static final String LOGIN_HISTORY_ENDPOINT         = API_V1 + "/login-history";
    public static final String USER_SESSIONS_ENDPOINT         = API_V1 + "/user-sessions";

    // ----------------------------------------------------------------
    // Roles
    // ----------------------------------------------------------------
    public static final String ROLE_ADMIN    = "ADMIN";
    public static final String ROLE_MANAGER  = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_FINANCE  = "FINANCE";
    public static final String ROLE_VENDOR   = "VENDOR";

    // ----------------------------------------------------------------
    // JWT
    // ----------------------------------------------------------------
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX        = "Bearer ";

    // ----------------------------------------------------------------
    // Document Number Prefixes
    // ----------------------------------------------------------------
    public static final String PR_PREFIX  = "PR-";
    public static final String PO_PREFIX  = "PO-";
    public static final String INV_PREFIX = "INV-";
    public static final String PAY_PREFIX = "PAY-";

    // ----------------------------------------------------------------
    // Validation Messages
    // ----------------------------------------------------------------
    public static final String MSG_REQUIRED        = " is required";
    public static final String MSG_INVALID_EMAIL   = "Invalid email format";
    public static final String MSG_POSITIVE        = " must be a positive number";
    public static final String MSG_NOT_BLANK       = " must not be blank";

    // ----------------------------------------------------------------
    // Login Status
    // ----------------------------------------------------------------
    public static final String LOGIN_SUCCESS = "SUCCESS";
    public static final String LOGIN_FAILED  = "FAILED";
}
