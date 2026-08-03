import { Routes, Route } from 'react-router-dom';

import LandingPage from '../pages/Landing/LandingPage';
import InternalLogin from '../pages/Authentication/InternalLogin';
import VendorLogin from '../pages/Authentication/VendorLogin';
import VendorRegister from '../pages/Authentication/VendorRegister';
import Register from '../pages/Authentication/Register';
import ForgotPassword from '../pages/Authentication/ForgotPassword';
import OTPVerification from '../pages/Authentication/OTPVerification';
import ResetPassword from '../pages/Authentication/ResetPassword';
import Unauthorized from '../pages/Authentication/Unauthorized';
import NotFound from '../pages/Authentication/NotFound';

import DashboardLayout from '../components/layouts/DashboardLayout/DashboardLayout';
import ProtectedRoute from './ProtectedRoute';

import EmployeeDashboard from '../pages/Dashboard/EmployeeDashboard';
import ManagerDashboard from '../pages/Dashboard/ManagerDashboard';
import ProcurementOfficerDashboard from '../pages/Dashboard/ProcurementOfficerDashboard';
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

import ApprovalHistory from '../pages/ApprovalHistory';
import DepartmentRequests from '../pages/DepartmentRequests';
import PurchaseRequests from '../pages/PurchaseRequests';
import PurchaseOrders from '../pages/PurchaseOrders';
import Suppliers from '../pages/Suppliers';
import Reports from '../pages/Reports';

import { USER_ROLES } from '../utils/constants';

const EMPLOYEE_ROLES = [
  USER_ROLES.EMPLOYEE,
  USER_ROLES.PROCUREMENT_OFFICER,
];

const AuthRoutes = () => {
  return (
    <Routes>

      {/* ================= PUBLIC ROUTES ================= */}

      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<InternalLogin />} />
      <Route path="/vendor-login" element={<VendorLogin />} />
      <Route path="/vendor/register" element={<VendorRegister />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<OTPVerification />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* ================= DASHBOARD ================= */}

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >

        {/* Employee */}
        <Route
          path="employee"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.EMPLOYEE]}>
              <EmployeeDashboard />
            </ProtectedRoute>
          }
        />

        {/* Manager */}
        <Route
          path="manager"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
              <ManagerDashboard />
            </ProtectedRoute>
          }
        />

        {/* Procurement Officer */}
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

        {/* Vendor */}
        <Route
          path="vendor"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.VENDOR]}>
              <VendorDashboard />
            </ProtectedRoute>
          }
        />

        {/* Manager Pages */}

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

        {/* Finance */}

        <Route
          path="finance"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.FINANCE]}>
              <FinanceDashboard />
            </ProtectedRoute>
          }
        />

        {/* Admin */}

        <Route
          path="admin"
          element={
            <ProtectedRoute allowedRoles={[USER_ROLES.ADMIN]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

      </Route>

      {/* ================= EMPLOYEE ================= */}

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
        <Route
          path="purchase-requests/create"
          element={<CreatePurchaseRequestPage />}
        />
        <Route
          path="purchase-requests/:id"
          element={<RequestDetailsPage />}
        />
      </Route>

      {/* ================= MANAGER ================= */}

      <Route
        path="/manager"
        element={
          <ProtectedRoute allowedRoles={[USER_ROLES.MANAGER]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route
          path="requests/:id"
          element={<RequestDetailsPage />}
        />
      </Route>

      {/* ================= VENDOR ================= */}

      <Route
        path="/vendor"
        element={
          <ProtectedRoute allowedRoles={[USER_ROLES.VENDOR]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route
          path="purchase-orders"
          element={<VendorPurchaseOrdersPage />}
        />

        <Route
          path="purchase-orders/:id"
          element={<VendorPurchaseOrderDetailPage />}
        />

        <Route
          path="deliveries"
          element={<VendorUpdateDeliveryPage />}
        />

        <Route
          path="profile"
          element={<VendorProfilePage />}
        />
      </Route>

      {/* ================= NOTIFICATIONS ================= */}

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

      {/* ================= 404 ================= */}

      <Route path="*" element={<NotFound />} />

    </Routes>
  );
};

export default AuthRoutes;