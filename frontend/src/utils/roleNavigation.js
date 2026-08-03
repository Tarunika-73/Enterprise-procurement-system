import { USER_ROLES } from './constants';

const ROLE_ROUTE_MAP = {
  [USER_ROLES.EMPLOYEE]: '/dashboard/employee',
  [USER_ROLES.MANAGER]: '/dashboard/manager',
  [USER_ROLES.VENDOR]: '/dashboard/vendor',
  [USER_ROLES.FINANCE]: '/finance',
  [USER_ROLES.ADMIN]: '/dashboard/admin',
  [USER_ROLES.PROCUREMENT_OFFICER]: '/dashboard/procurement-officer',
};

/**
 * Maps every possible backend role string to a frontend USER_ROLES value.
 * Keys are lowercased for case-insensitive matching.
 */
const ROLE_ALIAS_MAP = {
  'admin': USER_ROLES.ADMIN,
  'employee': USER_ROLES.EMPLOYEE,
  'manager': USER_ROLES.MANAGER,
  'department manager': USER_ROLES.MANAGER,
  'vendor': USER_ROLES.VENDOR,
  'finance': USER_ROLES.FINANCE,
  'finance officer': USER_ROLES.FINANCE,
  'procurement officer': USER_ROLES.PROCUREMENT_OFFICER,
};

/**
 * Normalizes any backend role string to a frontend USER_ROLES value.
 */
export const normalizeRole = (role) => {
  if (!role) return null;
  const key = role.trim().toLowerCase();
  return ROLE_ALIAS_MAP[key] ?? null;
};

/**
 * Resolves the post-login navigation path based on the role returned by the backend.
 */
export const getDashboardRouteByRole = (role) => {
  const normalizedRole = normalizeRole(role);
  return ROLE_ROUTE_MAP[normalizedRole] || '/unauthorized';
};
