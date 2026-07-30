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
import ProcurementOfficerDashboard from '../pages/Dashboard/ProcurementOfficerDashboard';
import ProtectedRoute from './ProtectedRoute';
import { USER_ROLES } from '../utils/constants';
import ApprovalHistory from '../pages/ApprovalHistory';
import DepartmentRequests from '../pages/DepartmentRequests';
import PurchaseRequests from '../pages/PurchaseRequests';
import PurchaseOrders from '../pages/PurchaseOrders';
import Suppliers from '../pages/Suppliers';
import Reports from '../pages/Reports';

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
  path="procurement-officer"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.PROCUREMENT_OFFICER]}>
      <ProcurementOfficerDashboard />
    </ProtectedRoute>
  }
/>
<Route
  path="purchase-requests"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.PROCUREMENT_OFFICER]}>
      <PurchaseRequests />
    </ProtectedRoute>
  }
/>

<Route
  path="purchase-orders"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.PROCUREMENT_OFFICER]}>
      <PurchaseOrders />
    </ProtectedRoute>
  }
/>

<Route
  path="vendor-management"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.PROCUREMENT_OFFICER]}>
      <Suppliers />
    </ProtectedRoute>
  }
/>

<Route
  path="reports"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.PROCUREMENT_OFFICER]}>
      <Reports />
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
<Route
  path="approval-history"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
      <ApprovalHistory />
    </ProtectedRoute>
  }
/>

<Route
  path="department-requests"
  element={
    <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
      <DepartmentRequests />
    </ProtectedRoute>
  }
/>
        <Route
          path="finance"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.FINANCE]}>
              <FinanceDashboard />
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
