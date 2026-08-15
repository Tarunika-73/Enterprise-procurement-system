/**
 * Application-wide constants for the Enterprise Procurement System.
 * Centralized here so future modules can import shared values without duplication.
 */

export const APP_NAME = 'Enterprise Procurement System';
export const APP_SHORT_NAME = 'EPS';
export const APP_VERSION = '1.0.0';
export const COPYRIGHT = `© ${new Date().getFullYear()} Infosys. All rights reserved.`;

export const API_BASE_URL = '';

/** User roles returned by the Spring Boot backend */
export const USER_ROLES = {
  ADMIN: 'Admin',
  EMPLOYEE: 'Employee',
  MANAGER: 'Manager',
  VENDOR: 'Vendor',
  FINANCE: 'Finance',
  PROCUREMENT_OFFICER: 'Procurement Officer',
};

/** Roles available during self-registration */
export const REGISTRATION_ROLES = [
  USER_ROLES.EMPLOYEE,
  USER_ROLES.MANAGER,
  USER_ROLES.VENDOR,
  USER_ROLES.FINANCE,
  USER_ROLES.PROCUREMENT_OFFICER,
];

/** OTP configuration */
export const OTP_LENGTH = 6;
export const OTP_RESEND_SECONDS = 60;

/** Session storage keys — used until JWT is persisted via backend contract */
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'eps_auth_token',
  USER: 'eps_user',
  RESET_EMAIL: 'eps_reset_email',
};
