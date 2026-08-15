import api from './api';

const BASE = '/v1/admin';

export const getAdminDashboardStats = () =>
  api.get(`${BASE}/dashboard`).then((r) => r.data);

// Users
export const getAdminUsers = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/users`, { params: { page, size, sort } }).then((r) => r.data);

export const getAdminUserById = (id) =>
  api.get(`${BASE}/users/${id}`).then((r) => r.data);

export const activateUser = (id) =>
  api.put(`${BASE}/users/${id}/activate`).then((r) => r.data);

export const deactivateUser = (id) =>
  api.put(`${BASE}/users/${id}/deactivate`).then((r) => r.data);

export const deleteUser = (id) =>
  api.delete(`${BASE}/users/${id}`).then((r) => r.data);

// Vendors
export const getAdminVendors = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/vendors`, { params: { page, size } }).then((r) => r.data);

export const getAdminVendorById = (id) =>
  api.get(`${BASE}/vendors/${id}`).then((r) => r.data);

export const activateVendor = (id) =>
  api.put(`${BASE}/vendors/${id}/activate`).then((r) => r.data);

export const deactivateVendor = (id) =>
  api.put(`${BASE}/vendors/${id}/deactivate`).then((r) => r.data);

// Departments
export const getAdminDepartments = ({ page = 0, size = 20 } = {}) =>
  api.get(`${BASE}/departments`, { params: { page, size } }).then((r) => r.data);

export const createAdminDepartment = (payload) =>
  api.post(`${BASE}/departments`, payload).then((r) => r.data);

export const updateAdminDepartment = (id, payload) =>
  api.put(`${BASE}/departments/${id}`, payload).then((r) => r.data);

export const deleteAdminDepartment = (id) =>
  api.delete(`${BASE}/departments/${id}`).then((r) => r.data);

// Products
export const getAdminProducts = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/products`, { params: { page, size } }).then((r) => r.data);

export const createAdminProduct = (payload) =>
  api.post(`${BASE}/products`, payload).then((r) => r.data);

export const updateAdminProduct = (id, payload) =>
  api.put(`${BASE}/products/${id}`, payload).then((r) => r.data);

export const deleteAdminProduct = (id) =>
  api.delete(`${BASE}/products/${id}`).then((r) => r.data);

// Purchase Requests
export const getAdminPurchaseRequests = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/purchase-requests`, { params: { page, size } }).then((r) => r.data);

// Purchase Orders
export const getAdminPurchaseOrders = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/purchase-orders`, { params: { page, size } }).then((r) => r.data);

// Invoices
export const getAdminInvoices = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/invoices`, { params: { page, size } }).then((r) => r.data);

// Payments
export const getAdminPayments = ({ page = 0, size = 10 } = {}) =>
  api.get(`${BASE}/payments`, { params: { page, size } }).then((r) => r.data);

// Audit Logs
export const getAdminAuditLogs = ({ page = 0, size = 20 } = {}) =>
  api.get(`${BASE}/audit-logs`, { params: { page, size } }).then((r) => r.data);
