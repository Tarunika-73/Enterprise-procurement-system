import api from './api';

/**
 * Request AI vendor recommendation for a specific approved purchase request.
 * The backend derives product and quantity from the purchase request itself.
 * This is advisory only — it does NOT assign a vendor or create a PO.
 */
export const recommendVendorForRequest = (purchaseRequestId) =>
  api.post(`/v1/procurement/purchase-requests/${purchaseRequestId}/recommend-vendor`)
    .then((r) => r.data);
