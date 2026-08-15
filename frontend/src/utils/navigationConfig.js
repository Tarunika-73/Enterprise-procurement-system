import { USER_ROLES } from './constants';
import { getDashboardRouteByRole, normalizeRole } from './roleNavigation';

const ALL_INTERNAL = [
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
    roles: [USER_ROLES.ADMIN, ...ALL_INTERNAL, USER_ROLES.VENDOR],
    resolvePath: (role) => getDashboardRouteByRole(role),
  },
  {
    id: 'products',
    label: 'Products',
    icon: 'bi-box-seam',
    path: '/employee/products',
    roles: [USER_ROLES.EMPLOYEE],
  },
  {
    id: 'create-request',
    label: 'Create Request',
    icon: 'bi-plus-circle',
    path: '/employee/purchase-requests/create',
    roles: [USER_ROLES.EMPLOYEE],
  },
  {
    id: 'my-requests',
    label: 'My Requests',
    icon: 'bi-file-earmark-text',
    path: '/employee/purchase-requests',
    roles: [USER_ROLES.EMPLOYEE],
  },
  {
    // Team Requests — manager inbox for approving department requests.
    id: 'purchase-requests',
    label: 'Team Requests',
    icon: 'bi-clipboard-check',
    path: '/dashboard/team-requests',
    roles: [USER_ROLES.MANAGER],
  },
  {
    id: 'admin-users', label: 'Users', icon: 'bi-people', path: '/admin/users', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-vendors', label: 'Vendors', icon: 'bi-building', path: '/admin/vendors', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-departments', label: 'Departments', icon: 'bi-diagram-3', path: '/admin/departments', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-products', label: 'Products', icon: 'bi-box-seam', path: '/admin/products', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-purchase-requests',
    label: 'Purchase Requests',
    icon: 'bi-clipboard-check',
    path: '/admin/purchase-requests',
    roles: [USER_ROLES.ADMIN],
  },
  {
    // Procurement Officer — approved requests to process
    id: 'procurement-purchase-requests',
    label: 'Purchase Requests',
    icon: 'bi-clipboard-check',
    path: '/dashboard/purchase-requests',
    roles: [USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: false,
  },
  {
    id: 'admin-purchase-orders', label: 'Purchase Orders', icon: 'bi-cart-check', path: '/admin/purchase-orders', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-invoices', label: 'Invoices', icon: 'bi-receipt', path: '/admin/invoices', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-payments', label: 'Payments', icon: 'bi-cash-stack', path: '/admin/payments', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-audit-logs', label: 'Audit Logs', icon: 'bi-journal-text', path: '/admin/audit-logs', roles: [USER_ROLES.ADMIN],
  },
  {
    id: 'admin-profile', label: 'Profile', icon: 'bi-person-circle', path: '/admin/profile', roles: [USER_ROLES.ADMIN],
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
    id: 'purchase-orders',
    label: 'Purchase Orders',
    icon: 'bi-cart-check',
    path: '/dashboard/purchase-orders',
    roles: [USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: false,
  },
  {
    id: 'goods-receipts', label: 'Goods Receipts', icon: 'bi-clipboard-check',
    path: '/dashboard/goods-receipts', roles: [USER_ROLES.PROCUREMENT_OFFICER],
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
    comingSoon: false,
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
    roles: [USER_ROLES.PROCUREMENT_OFFICER],
    comingSoon: false,
  },
  {
    id: 'finance-pending',
    label: 'Pending Payments',
    icon: 'bi-hourglass-split',
    path: '/finance/pending-payments',
    roles: [USER_ROLES.FINANCE],
  },
  {
    id: 'finance-history',
    label: 'Payment History',
    icon: 'bi-clock-history',
    path: '/finance/payment-history',
    roles: [USER_ROLES.FINANCE],
  },
  {
    id: 'finance-invoices', label: 'Invoices', icon: 'bi-receipt', path: '/finance/invoices', roles: [USER_ROLES.FINANCE],
  },
  {
    id: 'finance-reports',
    label: 'Finance Reports',
    icon: 'bi-bar-chart-line',
    path: '/finance/reports',
    roles: [],
  },
  {
    id: 'reports',
    label: 'Reports',
    icon: 'bi-bar-chart-line',
    path: '/dashboard/reports',
    roles: ALL_INTERNAL,
    comingSoon: false,
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
    roles: [],
    comingSoon: true,
  },
  {
    id: 'settings',
    label: 'Settings',
    icon: 'bi-gear',
    path: '/settings',
    roles: [USER_ROLES.FINANCE],
    comingSoon: true,
  },
];

export const getNavItemsForRole = (role) => {
  if (!role) return NAV_ITEMS.filter((item) => item.id === 'dashboard');
  const normalizedRole = normalizeRole(role);
  return NAV_ITEMS.filter((item) =>
    item.roles.some((allowedRole) => allowedRole === normalizedRole)
  );
};

export const resolveNavItemPath = (item, role) => {
  if (item.resolvePath) return item.resolvePath(role);
  return item.path;
};
