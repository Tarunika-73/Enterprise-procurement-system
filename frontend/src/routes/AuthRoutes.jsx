import { Routes, Route } from 'react-router-dom';
import LandingPage from '../pages/Landing/LandingPage';
import InternalLogin from '../pages/Authentication/InternalLogin';
import VendorLogin from '../pages/Authentication/VendorLogin';
import Register from '../pages/Authentication/Register';
import ForgotPassword from '../pages/Authentication/ForgotPassword';
import OTPVerification from '../pages/Authentication/OTPVerification';
import ResetPassword from '../pages/Authentication/ResetPassword';
import Unauthorized from '../pages/Authentication/Unauthorized';
import NotFound from '../pages/Authentication/NotFound';
import DashboardLayout from '../components/layouts/DashboardLayout/DashboardLayout';
import EmployeeDashboard from '../pages/Dashboard/EmployeeDashboard';
import ManagerDashboard from '../pages/Dashboard/ManagerDashboard';
import VendorDashboard from '../pages/Dashboard/VendorDashboard';
import FinanceDashboard from '../pages/Dashboard/FinanceDashboard';
import AdminDashboard from '../pages/Dashboard/AdminDashboard';
import PurchaseOrdersPage from '../pages/Finance/PurchaseOrdersPage';
import InvoiceManagementPage from '../pages/Finance/InvoiceManagementPage';
import PaymentManagementPage from '../pages/Finance/PaymentManagementPage';
import VendorPaymentsPage from '../pages/Finance/VendorPaymentsPage';
import ExpenseDashboardPage from '../pages/Finance/ExpenseDashboardPage';
import FinancialReportsPage from '../pages/Finance/FinancialReportsPage';
import AuditLogsPage from '../pages/Finance/AuditLogsPage';
import NotificationsPage from '../pages/Finance/NotificationsPage';
import ProfilePage from '../pages/Finance/ProfilePage';
import ProtectedRoute from './ProtectedRoute';
import { USER_ROLES } from '../utils/constants';

const FINANCE_ROLES = [
  USER_ROLES.FINANCE,
  USER_ROLES.FINANCE_MANAGER,
  USER_ROLES.FINANCE_OFFICER,
  USER_ROLES.ACCOUNTS_EXECUTIVE,
  USER_ROLES.ADMIN,
  USER_ROLES.MANAGER,
];

const AuthRoutes = () => {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<InternalLogin />} />
      <Route path="/vendor-login" element={<VendorLogin />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<OTPVerification />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* Protected dashboard routes */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route
          path="employee"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.EMPLOYEE]}>
              <EmployeeDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="manager"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
              <ManagerDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="vendor"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.VENDOR]}>
              <VendorDashboard />
            </ProtectedRoute>
          }
        />
        
        {/* Finance Sub-routes */}
        <Route
          path="finance"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <FinanceDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/purchase-orders"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <PurchaseOrdersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/invoices"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <InvoiceManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/payments"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <PaymentManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/vendor-payments"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <VendorPaymentsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/expense-reports"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <ExpenseDashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/reports"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <FinancialReportsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/audit-logs"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <AuditLogsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/notifications"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <NotificationsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="finance/profile"
          element={
            <ProtectedRoute allowedRoles={FINANCE_ROLES}>
              <ProfilePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="admin"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.ADMIN]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AuthRoutes;
