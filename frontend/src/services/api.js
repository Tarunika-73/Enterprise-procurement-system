import axios from 'axios';
import { STORAGE_KEYS } from '../utils/constants';

// Uses Vite proxy: /api → http://localhost:8080/api
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request if present
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401, clear session and redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
      // localStorage.removeItem(STORAGE_KEYS.USER);
      // window.location.href = '/login';
      console.log("===== 401 ERROR =====");
      console.log(error.response);
      console.log("Request URL:", error.config?.url);
      console.log("Authorization Header:", error.config?.headers?.Authorization);

      debugger;
    }
    return Promise.reject(error);
  }
);

export default api;
