import api from './api';

const BASE = '/v1/procurement';

export const getProcurementDashboardStats = () =>
  api.get(`${BASE}/dashboard-stats`).then((r) => r.data);

export const getApprovedPurchaseRequests = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/purchase-requests`, { params: { page, size, sort } }).then((r) => r.data);

export const getApprovedRequestById = (id) =>
  api.get(`${BASE}/purchase-requests/${id}`).then((r) => r.data);

export const createPurchaseOrder = (payload) =>
  api.post(`${BASE}/purchase-orders`, payload).then((r) => r.data);

export const getAllPurchaseOrders = ({ page = 0, size = 10, sort = 'createdAt,desc' } = {}) =>
  api.get(`${BASE}/purchase-orders`, { params: { page, size, sort } }).then((r) => r.data);

export const getPurchaseOrderById = (id) =>
  api.get(`${BASE}/purchase-orders/${id}`).then((r) => r.data);

export const assignVendorToPO = (id, vendorId) =>
  api.patch(`${BASE}/purchase-orders/${id}/assign-vendor`, { vendorId }).then((r) => r.data);

export const sendPurchaseOrder = (id) =>
  api.patch(`${BASE}/purchase-orders/${id}/send`).then((r) => r.data);

export const getActiveVendors = ({ page = 0, size = 50 } = {}) =>
  api.get(`${BASE}/vendors`, { params: { page, size } }).then((r) => r.data);

export const getGoodsReceiptWorkflows = () => api.get('/v1/receipts/workflow').then((r) => r.data);
export const getGoodsReceiptWorkflow = (deliveryId) => api.get(`/v1/receipts/workflow/${deliveryId}`).then((r) => r.data);
export const createGoodsReceipt = (payload) => api.post('/v1/receipts', payload).then((r) => r.data);
export const downloadGoodsReceiptPdf = (receiptId) =>
  api.get(`/v1/receipts/${receiptId}/pdf`, { responseType: 'blob' }).then((r) => r.data);
