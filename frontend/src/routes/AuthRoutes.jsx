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
import ProductListPage from '../pages/Employee/ProductListPage';
import CreatePurchaseRequestPage from '../pages/Employee/CreatePurchaseRequestPage';
import MyRequestsPage from '../pages/Employee/MyRequestsPage';
import RequestDetailsPage from '../pages/Employee/RequestDetailsPage';
import NotificationsPage from '../pages/Employee/NotificationsPage';
import VendorPurchaseOrdersPage from '../pages/Vendor/VendorPurchaseOrdersPage';
import VendorPurchaseOrderDetailPage from '../pages/Vendor/VendorPurchaseOrderDetailPage';
import VendorUpdateDeliveryPage from '../pages/Vendor/VendorUpdateDeliveryPage';
import VendorProfilePage from '../pages/Vendor/VendorProfilePage';
import VendorRegister from '../pages/Authentication/VendorRegister';
import ProtectedRoute from './ProtectedRoute';
import { USER_ROLES } from '../utils/constants';

const EMPLOYEE_ROLES = [USER_ROLES.EMPLOYEE, USER_ROLES.PROCUREMENT_OFFICER];

const AuthRoutes = () => {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<InternalLogin />} />
      <Route path="/vendor-login" element={<VendorLogin />} />
      <Route path="/vendor/register" element={<VendorRegister />} />
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

      {/* Employee module — same dashboard layout */}
      <Route
        path="/employee"
        element={
          <ProtectedRoute allowedRoles={EMPLOYEE_ROLES}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route path="products" element={<ProductListPage />} />
        <Route path="purchase-requests" element={<MyRequestsPage />} />
        <Route path="purchase-requests/create" element={<CreatePurchaseRequestPage />} />
        <Route path="purchase-requests/:id" element={<RequestDetailsPage />} />
      </Route>

      <Route
        path="/manager"
        element={
          <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route path="requests/:id" element={<RequestDetailsPage />} />
      </Route>

      <Route
        path="/vendor"
        element={
          <ProtectedRoute allowedRoles={[USER_ROLES.VENDOR]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route path="purchase-orders" element={<VendorPurchaseOrdersPage />} />
        <Route path="purchase-orders/:id" element={<VendorPurchaseOrderDetailPage />} />
        <Route path="deliveries" element={<VendorUpdateDeliveryPage />} />
        <Route path="profile" element={<VendorProfilePage />} />
      </Route>

      <Route
        path="/notifications"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<NotificationsPage />} />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AuthRoutes;
