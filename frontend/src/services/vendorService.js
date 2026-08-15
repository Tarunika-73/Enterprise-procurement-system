import api from './api';

const BASE = '/v1/vendor-portal';

export const getVendorDashboard = () =>
  api.get(`${BASE}/dashboard`).then((r) => r.data);

export const getVendorPurchaseOrders = ({ page = 0, size = 10, sort = 'createdAt,desc', status } = {}) => {
  const params = { page, size, sort };
  if (status) params.status = status;
  return api.get(`${BASE}/purchase-orders`, { params }).then((r) => r.data);
};

export const getVendorPurchaseOrderDetail = (id) =>
  api.get(`${BASE}/purchase-orders/${id}`).then((r) => r.data);

export const acceptVendorOrder = (id) =>
  api.post(`${BASE}/purchase-orders/${id}/accept`).then((r) => r.data);

export const rejectVendorOrder = (id, remarks) =>
  api.post(`${BASE}/purchase-orders/${id}/reject`, { remarks }).then((r) => r.data);

export const updateVendorDelivery = (payload) =>
  api.post(`${BASE}/deliveries`, payload).then((r) => r.data);

export const getVendorProfile = () =>
  api.get(`${BASE}/profile`).then((r) => r.data);

export const updateVendorProfile = (payload) =>
  api.put(`${BASE}/profile`, payload).then((r) => r.data);

export const getVendorInvoices = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/invoices`, { params: { page, size, sort } }).then((r) => r.data);
