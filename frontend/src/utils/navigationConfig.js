import { USER_ROLES } from './constants';
import { getDashboardRouteByRole, normalizeRole } from './roleNavigation';

/**
 * Sidebar navigation configuration.
 * Extend this file when new modules (Purchase Requests, Vendors, etc.) are added.
 * Role filtering is applied at render time — no hardcoded sidebar per role.
 */

const ALL_INTERNAL = [
  USER_ROLES.ADMIN,
  USER_ROLES.EMPLOYEE,
  USER_ROLES.MANAGER,
  USER_ROLES.FINANCE,
  USER_ROLES.PROCUREMENT_OFFICER,
];

export const NAV_ITEMS = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    icon: 'bi-speedometer2',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
    resolvePath: (role) => getDashboardRouteByRole(role),
  },
  {
    id: 'purchase-requests',
    label: 'Purchase Requests',
    icon: 'bi-file-earmark-text',
    path: '/purchase-requests',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.MANAGER, USER_ROLES.ADMIN, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: true,
  },
  {
    id: 'purchase-orders',
    label: 'Purchase Orders',
    icon: 'bi-cart-check',
    path: '/dashboard/finance/purchase-orders',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
  },
  {
    id: 'invoices',
    label: 'Invoices',
    icon: 'bi-receipt',
    path: '/dashboard/finance/invoices',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
  },
  {
    id: 'payments',
    label: 'Payments',
    icon: 'bi-credit-card',
    path: '/dashboard/finance/payments',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
  },
  {
    id: 'vendor-payments',
    label: 'Vendor Payments',
    icon: 'bi-wallet2',
    path: '/dashboard/finance/vendor-payments',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
  },
  {
    id: 'expense-reports',
    label: 'Expense Dashboard',
    icon: 'bi-pie-chart-fill',
    path: '/dashboard/finance/expense-reports',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
  },
  {
    id: 'reports',
    label: 'Financial Reports',
    icon: 'bi-bar-chart-line',
    path: '/dashboard/finance/reports',
    roles: ALL_INTERNAL,
  },
  {
    id: 'audit-logs',
    label: 'Audit Logs',
    icon: 'bi-shield-check',
    path: '/dashboard/finance/audit-logs',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN],
  },
  {
    id: 'notifications',
    label: 'Notifications',
    icon: 'bi-bell',
    path: '/dashboard/finance/notifications',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
  },
  {
    id: 'profile',
    label: 'Profile',
    icon: 'bi-person-circle',
    path: '/dashboard/finance/profile',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
  },
];

/**
 * Returns navigation items visible to the given user role.
 * @param {string|null} role
 * @returns {Array}
 */
export const getNavItemsForRole = (role) => {
  if (!role) return NAV_ITEMS.filter((item) => item.id === 'dashboard');

  const normalizedRole = normalizeRole(role);

  return NAV_ITEMS.filter((item) =>
    item.roles.some((allowedRole) => allowedRole === normalizedRole)
  );
};

/**
 * Resolves the href for a navigation item based on user role.
 */
export const resolveNavItemPath = (item, role) => {
  if (item.resolvePath) {
    return item.resolvePath(role);
  }
  return item.path;
};
