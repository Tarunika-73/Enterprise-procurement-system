import api from './api';

/**
 * Vendor-facing API service.
 * Wraps /vendors, /supplier-performance and /supplier-compliance endpoints
 * consumed by the Vendor role (dashboard, profile, compliance, PO/invoice previews).
 */

// ---------- Vendors ----------

export const getAllVendors = async (params = {}) => {
  const response = await api.get('/vendors', { params });
  return response.data;
};

export const getVendorById = async (id) => {
  const response = await api.get(`/vendors/${id}`);
  return response.data;
};

export const updateVendor = async (id, payload) => {
  const response = await api.put(`/vendors/${id}`, payload);
  return response.data;
};

/**
 * The backend has no "get vendor by current user" endpoint yet, so we resolve
 * the logged-in vendor's record by matching their account email against the
 * vendor list. Pages large enough to cover realistic vendor counts.
 */
export const findVendorByEmail = async (email) => {
  if (!email) return null;

  const normalizedEmail = email.trim().toLowerCase();
  const response = await api.get('/vendors', { params: { page: 0, size: 200 } });
  const page = response.data?.data ?? response.data;
  const vendors = page?.content ?? [];

  return vendors.find((vendor) => vendor.email?.trim().toLowerCase() === normalizedEmail) ?? null;
};

// ---------- Supplier Performance ----------
// Note: SupplierPerformanceController is only mapped at /v1/supplier-performance
// (no un-prefixed alias like VendorController/SupplierComplianceController have),
// so the /v1 prefix here is required or every call 404s.

export const getVendorPerformance = async (vendorId, params = {}) => {
  const response = await api.get(`/v1/supplier-performance/vendor/${vendorId}`, { params });
  return response.data;
};

export const getVendorAverageRating = async (vendorId) => {
  const response = await api.get(`/v1/supplier-performance/vendor/${vendorId}/average-rating`);
  return response.data;
};

// ---------- Supplier Compliance ----------

export const getVendorCompliance = async (vendorId, params = {}) => {
  const response = await api.get(`/supplier-compliance/vendor/${vendorId}`, { params });
  return response.data;
};

export default {
  getAllVendors,
  getVendorById,
  updateVendor,
  findVendorByEmail,
  getVendorPerformance,
  getVendorAverageRating,
  getVendorCompliance,
};
