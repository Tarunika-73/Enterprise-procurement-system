import axios from 'axios';

// Uses Vite proxy: /api → http://localhost:8080/api
const authApi = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

export const login = async (credentials) => {
  const response = await authApi.post('/auth/login', credentials);
  return response.data;
};

export const register = async (userData) => {
  const response = await authApi.post('/auth/register', userData);
  return response.data;
};

export const forgotPassword = async (email) => {
  const response = await authApi.post('/auth/forgot-password', { email });
  return response.data;
};

export const verifyOTP = async (payload) => {
  const response = await authApi.post('/auth/verify-otp', payload);
  return response.data;
};

export const resetPassword = async (payload) => {
  const response = await authApi.post('/auth/reset-password', payload);
  return response.data;
};

export const checkEmailExists = async (email) => {
  const response = await authApi.get('/auth/check-email', { params: { email } });
  return response.data?.data?.exists ?? response.data?.exists ?? false;
};

export default { login, register, forgotPassword, verifyOTP, resetPassword, checkEmailExists };
