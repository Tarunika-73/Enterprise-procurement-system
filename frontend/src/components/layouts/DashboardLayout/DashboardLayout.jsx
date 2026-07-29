import { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from '../Sidebar/Sidebar';
import TopNavbar from '../TopNavbar/TopNavbar';
import Footer from '../Footer/Footer';

/** Page titles keyed by dashboard route — compatible with BrowserRouter */
const DASHBOARD_PAGE_TITLES = {
  '/dashboard/employee': 'Employee Dashboard',
  '/dashboard/manager': 'Manager Dashboard',
  '/dashboard/vendor': 'Vendor Dashboard',
  '/dashboard/finance': 'Finance Dashboard',
  '/dashboard/finance/purchase-orders': 'Purchase Orders',
  '/dashboard/finance/invoices': 'Invoice Management',
  '/dashboard/finance/payments': 'Payment Management',
  '/dashboard/finance/vendor-payments': 'Vendor Payments History',
  '/dashboard/finance/expense-reports': 'Expense Analytics & Dashboard',
  '/dashboard/finance/reports': 'Financial Reports Generator',
  '/dashboard/finance/audit-logs': 'System Audit Logs',
  '/dashboard/finance/notifications': 'Notifications & Alerts',
  '/dashboard/finance/profile': 'User Profile & Security',
  '/dashboard/admin': 'Admin Dashboard',
};

const DashboardLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { pathname } = useLocation();

  const pageTitle = DASHBOARD_PAGE_TITLES[pathname] ?? 'Dashboard';

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
