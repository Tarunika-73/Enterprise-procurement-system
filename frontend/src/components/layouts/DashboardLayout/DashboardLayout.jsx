import { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from '../Sidebar/Sidebar';
import TopNavbar from '../TopNavbar/TopNavbar';
import Footer from '../Footer/Footer';

/** Page titles keyed by route — compatible with BrowserRouter */
const DASHBOARD_PAGE_TITLES = {
  '/dashboard/employee': 'Employee Dashboard',
  '/dashboard/manager': 'Manager Dashboard',
  '/dashboard/vendor': 'Vendor Dashboard',
  '/dashboard/finance': 'Finance Dashboard',
  '/finance': 'Finance Dashboard',
  '/finance/pending-payments': 'Pending Payments',
  '/finance/payment-history': 'Payment History',
  '/finance/reports': 'Financial Reports',
  '/dashboard/admin': 'Admin Dashboard',
  '/employee/products': 'Products',
  '/employee/purchase-requests': 'My Requests',
  '/employee/purchase-requests/create': 'Create Purchase Request',
  '/notifications': 'Notifications',
  '/vendor/purchase-orders': 'Purchase Orders',
  '/vendor/deliveries': 'Update Delivery',
  '/vendor/profile': 'My Profile',
};

const resolvePageTitle = (pathname) => {
  if (DASHBOARD_PAGE_TITLES[pathname]) {
    return DASHBOARD_PAGE_TITLES[pathname];
  }
  if (pathname.startsWith('/employee/purchase-requests/')) {
    return 'Request Details';
  }
  if (pathname.startsWith('/manager/requests/')) {
    return 'Request Details';
  }
  if (pathname.startsWith('/vendor/purchase-orders/')) {
    return 'Purchase Order Details';
  }
  if (pathname.startsWith('/finance/payments/')) {
    return 'Payment Details';
  }
  return 'Dashboard';
};

const DashboardLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { pathname } = useLocation();

  const pageTitle = resolvePageTitle(pathname);

  return (
    <div className="dashboard-layout">
      <Sidebar
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      <div className="dashboard-main">
        <TopNavbar
          pageTitle={pageTitle}
          onToggleSidebar={() => setSidebarOpen((prev) => !prev)}
        />

        <main className="dashboard-content">
          <Outlet />
        </main>

        <Footer />
      </div>
    </div>
  );
};

export default DashboardLayout;
