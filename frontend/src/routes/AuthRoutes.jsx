import { Routes, Route } from 'react-router-dom';
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

const AuthRoutes = () => {
  return (
    <Routes>
      {/* Authentication Module Routes */}
      <Route path="/" element={<InternalLogin />} />
      <Route path="/vendor-login" element={<VendorLogin />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<OTPVerification />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* Dashboard Module — shared layout with nested role pages */}
      <Route path="/dashboard" element={<DashboardLayout />}>
        <Route
          path="employee"
          element={<EmployeeDashboard />}
          handle={{ title: 'Employee Dashboard' }}
        />
        <Route
          path="manager"
          element={<ManagerDashboard />}
          handle={{ title: 'Manager Dashboard' }}
        />
        <Route
          path="vendor"
          element={<VendorDashboard />}
          handle={{ title: 'Vendor Dashboard' }}
        />
        <Route
          path="finance"
          element={<FinanceDashboard />}
          handle={{ title: 'Finance Dashboard' }}
        />
        <Route
          path="admin"
          element={<AdminDashboard />}
          handle={{ title: 'Admin Dashboard' }}
        />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AuthRoutes;
