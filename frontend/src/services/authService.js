import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

const authApi = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

/**
 * Authentication service — all methods are API-ready placeholders.
 * Replace TODO blocks when Spring Boot endpoints are available.
 */

export const login = async (credentials) => {
  // TODO: Integrate Spring Boot API
  // POST /api/auth/login
  const response = await authApi.post('/api/auth/login', credentials);
  return response.data;
};

export const register = async (userData) => {
  // TODO: Integrate Spring Boot API
  // POST /api/auth/register
  const response = await authApi.post('/api/auth/register', userData);
  return response.data;
};

export const forgotPassword = async (email) => {
  // TODO: Integrate Spring Boot API
  // POST /api/auth/forgot-password
  const response = await authApi.post('/api/auth/forgot-password', { email });
  return response.data;
};

export const verifyOTP = async (payload) => {
  // TODO: Integrate Spring Boot API
  // POST /api/auth/verify-otp
  const response = await authApi.post('/api/auth/verify-otp', payload);
  return response.data;
};

export const resetPassword = async (payload) => {
  // TODO: Integrate Spring Boot API
  // POST /api/auth/reset-password
  const response = await authApi.post('/api/auth/reset-password', payload);
  return response.data;
};

export const checkEmailExists = async (email) => {
  // TODO: Integrate Spring Boot API
  // GET /api/auth/check-email?email={email}
  const response = await authApi.get('/api/auth/check-email', {
    params: { email },
  });
  return response.data?.exists ?? false;
};

export default {
  login,
  register,
  forgotPassword,
  verifyOTP,
  resetPassword,
  checkEmailExists,
};
