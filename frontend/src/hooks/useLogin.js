import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login as loginApi } from '../services/authService';
import { getApiErrorMessage } from '../utils/apiErrors';
import { getDashboardRouteByRole } from '../utils/roleNavigation';

/**
 * Shared login submission flow for internal and vendor login pages.
 * Calls the backend, stores auth state in context, and navigates by role.
 */
const useLogin = (loginType, errorFallback) => {
  const navigate = useNavigate();
  const { loginUser, setIsLoading, isLoading } = useAuth();
  const [toast, setToast] = useState({ show: false, message: '', type: 'danger' });

  const submitLogin = async ({ email, password }) => {
    setIsLoading(true);

    try {
      const response = await loginApi({
        email: email.trim(),
        password,
        loginType,
      });

      const payload = response?.data?.data ?? response?.data ?? response;
      const user = payload?.user;
      const token = payload?.token;

      if (!user || !token) {
        throw new Error('Invalid login response from server.');
      }

      loginUser({
        user,
        token,
      });

      const dashboardRoute = getDashboardRouteByRole(user.role);
      navigate(dashboardRoute, { replace: true });
    } catch (error) {
      setToast({
        show: true,
        message: getApiErrorMessage(error, errorFallback),
        type: 'danger',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const dismissToast = () => {
    setToast((prev) => ({ ...prev, show: false }));
  };

  return {
    submitLogin,
    isLoading,
    toast,
    dismissToast,
  };
};

export default useLogin;
