import api from './api';

const BASE = '/v1/finance';

export const getFinanceDashboard = () =>
  api.get(`${BASE}/dashboard`).then((r) => r.data);

export const getPendingPayments = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/pending-payments`, { params: { page, size, sort } }).then((r) => r.data);

export const getPaymentHistory = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/payment-history`, { params: { page, size, sort } }).then((r) => r.data);

export const getFinanceInvoices = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/invoices`, { params: { page, size, sort } }).then((r) => r.data);

export const getPaymentById = (id) =>
  api.get(`${BASE}/payments/${id}`).then((r) => r.data);

export const approvePayment = (purchaseOrderId, payload) =>
  api.post(`${BASE}/payments/${purchaseOrderId}/approve`, payload).then((r) => r.data);

export const cancelPayment = (purchaseOrderId) =>
  api.post(`${BASE}/payments/${purchaseOrderId}/cancel`).then((r) => r.data);
