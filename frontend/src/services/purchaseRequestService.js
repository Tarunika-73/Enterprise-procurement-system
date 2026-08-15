import api from './api';

/**
 * Purchase request APIs for the employee module.
 */
export const createPurchaseRequest = async (payload) => {
  const response = await api.post('/v1/purchase-requests', payload);
  return response.data;
};

export const getMyPurchaseRequests = async ({
  page = 0,
  size = 10,
  sort = 'createdAt,desc',
} = {}) => {
  const response = await api.get('/v1/purchase-requests/my', {
    params: { page, size, sort },
  });
  return response.data;
};

export const getPurchaseRequestById = async (id) => {
  const response = await api.get(`/v1/purchase-requests/${id}`);
  return response.data;
};

export const updatePurchaseRequest = async (id, payload) => {
  const response = await api.put(`/v1/purchase-requests/${id}`, payload);
  return response.data;
};

export const getEmployeeDashboardStats = async () => {
  const response = await api.get('/v1/purchase-requests/dashboard-stats');
  return response.data;
};

export const getAssignmentPreview = async () => {
  const response = await api.get('/v1/purchase-requests/assignment-preview');
  return response.data;
};

export const getManagerDashboardStats = async () => {
  const response = await api.get('/v1/purchase-requests/manager/dashboard-stats');
  return response.data;
};

export const getManagerInbox = async ({
  page = 0,
  size = 10,
  sort = 'createdAt,desc',
} = {}) => {
  const response = await api.get('/v1/purchase-requests/manager/inbox', {
    params: { page, size, sort },
  });
  return response.data;
};

export const approvePurchaseRequest = async (id, remarks = '') => {
  const response = await api.post(`/v1/purchase-requests/${id}/approve`, { remarks });
  return response.data;
};

export const rejectPurchaseRequest = async (id, remarks) => {
  const response = await api.post(`/v1/purchase-requests/${id}/reject`, { remarks });
  return response.data;
};

export const returnPurchaseRequest = async (id, remarks) => {
  const response = await api.post(`/v1/purchase-requests/${id}/return`, { remarks });
  return response.data;
};

export default {
  createPurchaseRequest,
  getMyPurchaseRequests,
  getPurchaseRequestById,
  updatePurchaseRequest,
  getEmployeeDashboardStats,
  getAssignmentPreview,
  getManagerDashboardStats,
  getManagerInbox,
  approvePurchaseRequest,
  rejectPurchaseRequest,
  returnPurchaseRequest,
};
