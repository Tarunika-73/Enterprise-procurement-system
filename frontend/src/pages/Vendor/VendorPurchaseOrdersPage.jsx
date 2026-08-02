import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import VendorPOTable from '../../components/vendor/VendorPOTable';
import { getVendorPurchaseOrders } from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getPageContent, getPageMeta } from '../../utils/employeeHelpers';

const STATUS_OPTIONS = [
  { value: '', label: 'All Statuses' },
  { value: 'SENT', label: 'Pending Delivery' },
  { value: 'ACCEPTED', label: 'Accepted' },
  { value: 'DELIVERED', label: 'Delivered' },
  { value: 'REJECTED', label: 'Rejected' },
  { value: 'CANCELLED', label: 'Cancelled' },
];

const VendorPurchaseOrdersPage = () => {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0 });
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getVendorPurchaseOrders({ page, size: 10, status: status || undefined });
      const payload = res?.data ?? res;
      setOrders(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load purchase orders.'));
      setOrders([]);
    } finally {
      setLoading(false);
    }
  }, [page, status]);

  useEffect(() => { load(); }, [load]);

  const handleStatusChange = (e) => {
    setStatus(e.target.value);
    setPage(0);
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Purchase Orders</h1>
        <p className="text-muted mb-0">Purchase orders received from the procurement team.</p>
      </div>

      {error ? <div className="alert alert-danger" role="alert">{error}</div> : null}

      <div className="employee-filter-bar mb-3 d-flex gap-3 flex-wrap align-items-center">
        <select
          className="form-select form-select-sm"
          style={{ maxWidth: 200 }}
          value={status}
          onChange={handleStatusChange}
          aria-label="Filter by status"
        >
          {STATUS_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>
        <span className="text-muted small ms-auto">
          {loading ? '' : `${meta.totalElements ?? orders.length} order(s)`}
        </span>
      </div>

      <VendorPOTable
        orders={orders}
        loading={loading}
        onView={(id) => navigate(`/vendor/purchase-orders/${id}`)}
      />

      {meta.totalPages > 1 && (
        <div className="d-flex justify-content-center gap-2 mt-3">
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            disabled={meta.number === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            Previous
          </button>
          <span className="align-self-center small text-muted">
            Page {meta.number + 1} of {meta.totalPages}
          </span>
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            disabled={meta.number >= meta.totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </>
  );
};

export default VendorPurchaseOrdersPage;
