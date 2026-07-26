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

      if (!response?.user || !response?.token) {
        throw new Error('Invalid login response from server.');
      }

      loginUser({
        user: response.user,
        token: response.token,
      });

      const dashboardRoute = getDashboardRouteByRole(response.user.role);
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
