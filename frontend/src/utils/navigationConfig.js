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
    path: '/purchase-orders',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.MANAGER, USER_ROLES.ADMIN, USER_ROLES.PROCUREMENT_OFFICER, USER_ROLES.VENDOR],
    comingSoon: true,
  },
  {
    id: 'vendor-management',
    label: 'Vendor Management',
    icon: 'bi-building',
    path: '/vendors',
    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: true,
  },
  {
    id: 'finance',
    label: 'Finance',
    icon: 'bi-currency-dollar',
    path: '/finance',
    roles: [USER_ROLES.FINANCE, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
    comingSoon: true,
  },
  {
    id: 'reports',
    label: 'Reports',
    icon: 'bi-bar-chart-line',
    path: '/reports',
    roles: ALL_INTERNAL,
    comingSoon: true,
  },
  {
    id: 'notifications',
    label: 'Notifications',
    icon: 'bi-bell',
    path: '/notifications',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
    comingSoon: true,
  },
  {
    id: 'feedback',
    label: 'Feedback & Complaints',
    icon: 'bi-chat-left-text',
    path: '/feedback',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
    comingSoon: true,
  },
  {
    id: 'settings',
    label: 'Settings',
    icon: 'bi-gear',
    path: '/settings',
    roles: [...ALL_INTERNAL, USER_ROLES.VENDOR],
    comingSoon: true,
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
