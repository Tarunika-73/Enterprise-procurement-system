import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login as loginApi } from '../services/authService';
import { getApiErrorMessage } from '../utils/apiErrors';
import { getDashboardRouteByRole } from '../utils/roleNavigation';

/**
 * Shared login submission flow for internal and vendor login pages.
 * Accepts an optional `apiFn` so vendor login can use a different endpoint
 * while employee login continues using the default /auth/login.
 */
const useLogin = (loginType, errorFallback, apiFn) => {
  const navigate = useNavigate();
  const { loginUser, setIsLoading, isLoading } = useAuth();
  const [toast, setToast] = useState({ show: false, message: '', type: 'danger' });

  const submitLogin = async ({ email, password }) => {
    setIsLoading(true);

    try {
      const callApi = apiFn ?? loginApi;
      const response = await callApi({ email: email.trim(), password, loginType });

      const payload = response?.data?.data ?? response?.data ?? response;

      // Vendor response uses `vendor` key; employee response uses `user` key
      const user  = payload?.user ?? payload?.vendor;
      const token = payload?.token;

      if (!user || !token) {
        throw new Error('Invalid login response from server.');
      }

      loginUser({ user, token });

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

  return { submitLogin, isLoading, toast, dismissToast };
};

export default useLogin;
