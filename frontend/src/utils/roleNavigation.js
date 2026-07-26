import { USER_ROLES } from './constants';

/**
 * Maps backend user roles to dashboard routes.
 * When new modules are added, extend this map — pages remain decoupled from login logic.
 */
const ROLE_ROUTE_MAP = {
  [USER_ROLES.EMPLOYEE]: '/dashboard/employee',
  [USER_ROLES.MANAGER]: '/dashboard/manager',
  [USER_ROLES.VENDOR]: '/dashboard/vendor',
  [USER_ROLES.FINANCE]: '/dashboard/finance',
  [USER_ROLES.ADMIN]: '/dashboard/admin',
  [USER_ROLES.PROCUREMENT_OFFICER]: '/dashboard/procurement',
};

/** Spring Boot enum-style roles → frontend display roles */
const BACKEND_ROLE_MAP = {
  ADMIN: USER_ROLES.ADMIN,
  EMPLOYEE: USER_ROLES.EMPLOYEE,
  MANAGER: USER_ROLES.MANAGER,
  VENDOR: USER_ROLES.VENDOR,
  FINANCE: USER_ROLES.FINANCE,
  PROCUREMENT_OFFICER: USER_ROLES.PROCUREMENT_OFFICER,
};

/**
 * Normalizes backend role strings to frontend USER_ROLES values.
 * Supports enum keys (ADMIN) and display values (Admin).
 */
export const normalizeRole = (role) => {
  if (!role) return null;

  const enumKey = role.toUpperCase().replace(/\s+/g, '_');
  if (BACKEND_ROLE_MAP[enumKey]) {
    return BACKEND_ROLE_MAP[enumKey];
  }

  return (
    Object.values(USER_ROLES).find(
      (value) => value.toLowerCase() === role.toLowerCase()
    ) ?? null
  );
};

/**
 * Resolves the post-login navigation path based on the role returned by the backend.
 * @param {string} role - Role string from Spring Boot login response
 * @returns {string} Dashboard route path
 */
export const getDashboardRouteByRole = (role) => {
  const normalizedRole = normalizeRole(role);
  return ROLE_ROUTE_MAP[normalizedRole] || '/unauthorized';
};
