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
    id: 'products',
    label: 'Products',
    icon: 'bi-box-seam',
    path: '/employee/products',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.PROCUREMENT_OFFICER],
  },
  {
    id: 'create-request',
    label: 'Create Request',
    icon: 'bi-plus-circle',
    path: '/employee/purchase-requests/create',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.PROCUREMENT_OFFICER],
  },
  {
    id: 'my-requests',
    label: 'My Requests',
    icon: 'bi-file-earmark-text',
<<<<<<< HEAD
    path: '/employee/purchase-requests',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.PROCUREMENT_OFFICER],
  },
  {
    id: 'purchase-requests',
    label: 'Team Requests',
    icon: 'bi-clipboard-check',
    path: '/dashboard/manager',
    roles: [USER_ROLES.MANAGER],
  },
  {
    id: 'admin-purchase-requests',
    label: 'Purchase Requests',
    icon: 'bi-clipboard-check',
    path: '/purchase-requests',
    roles: [USER_ROLES.ADMIN],
    comingSoon: true,
=======
    path: '/dashboard/purchase-requests',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.MANAGER, USER_ROLES.ADMIN, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: false,
>>>>>>> ae6bf03a5f7d17ff2bc197903ec351234d4c78e6
  },
  {
  id: 'approval-history',
  label: 'Approval History',
  icon: 'bi-clock-history',
  path: '/dashboard/approval-history',
  roles: [USER_ROLES.MANAGER],
  comingSoon: false,
},
{
  id: 'department-requests',
  label: 'Department Requests',
  icon: 'bi-building',
  path: '/dashboard/department-requests',
  roles: [USER_ROLES.MANAGER],
  comingSoon: false,
},
  {
    id: 'purchase-orders',
    label: 'Purchase Orders',
    icon: 'bi-cart-check',
<<<<<<< HEAD
    path: '/purchase-orders',
    roles: [USER_ROLES.MANAGER, USER_ROLES.ADMIN, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: true,
=======
   path: '/dashboard/purchase-orders',
    roles: [USER_ROLES.EMPLOYEE, USER_ROLES.MANAGER, USER_ROLES.ADMIN, USER_ROLES.PROCUREMENT_OFFICER, USER_ROLES.VENDOR],
    comingSoon: false,
>>>>>>> ae6bf03a5f7d17ff2bc197903ec351234d4c78e6
  },
  {
    id: 'vendor-purchase-orders',
    label: 'Purchase Orders',
    icon: 'bi-cart-check',
    path: '/vendor/purchase-orders',
    roles: [USER_ROLES.VENDOR],
  },
  {
    id: 'vendor-deliveries',
    label: 'Deliveries',
    icon: 'bi-truck',
    path: '/vendor/deliveries',
    roles: [USER_ROLES.VENDOR],
  },
  {
    id: 'vendor-invoices',
    label: 'Invoices',
    icon: 'bi-receipt',
    path: '/vendor/invoices',
    roles: [USER_ROLES.VENDOR],
    comingSoon: true,
  },
  {
    id: 'vendor-profile',
    label: 'Profile',
    icon: 'bi-person-circle',
    path: '/vendor/profile',
    roles: [USER_ROLES.VENDOR],
  },
  {
    id: 'vendor-management',
    label: 'Vendor Management',
    icon: 'bi-building',
    path: '/dashboard/vendor-management',
    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: false,
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
<<<<<<< HEAD
    path: '/reports',
    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.FINANCE, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: true,
=======
    path: '/dashboard/reports',
    roles: ALL_INTERNAL,
    comingSoon: false,
>>>>>>> ae6bf03a5f7d17ff2bc197903ec351234d4c78e6
  },
  {
    id: 'notifications',
    label: 'Notifications',
    icon: 'bi-bell',
    path: '/notifications',
    roles: [...ALL_INTERNAL],
  },
  {
    id: 'feedback',
    label: 'Feedback & Complaints',
    icon: 'bi-chat-left-text',
    path: '/feedback',
    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.FINANCE, USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: true,
  },
  {
    id: 'settings',
    label: 'Settings',
    icon: 'bi-gear',
    path: '/settings',
    roles: [USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.FINANCE, USER_ROLES.PROCUREMENT_OFFICER],
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
