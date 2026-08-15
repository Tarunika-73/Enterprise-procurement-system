import { useCallback, useEffect, useState } from 'react';
import { getAdminPurchaseRequests } from '../../services/adminService';
import {
  formatDate,
  formatCurrency,
  formatStatusLabel,
  getStatusBadgeClass,
  getPageContent,
  getPageMeta,
} from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminPurchaseRequestsPage = () => {
  const [requests, setRequests] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminPurchaseRequests({ page, size: 10 });
      const payload = res?.data ?? res;
      setRequests(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load purchase requests.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Purchase Requests</h1>
        <p className="text-muted mb-0">System-wide view of all purchase requests.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : requests.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-clipboard-data fs-1 d-block mb-2" />
              No purchase requests found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Request #</th>
                    <th>Title</th>
                    <th>Requester</th>
                    <th>Department</th>
                    <th>Priority</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Created</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((r) => (
                    <tr key={r.id}>
                      <td className="fw-semibold">{r.requestNumber}</td>
                      <td>{r.title ?? '—'}</td>
                      <td>{r.requesterName ?? '—'}</td>
                      <td>{r.departmentName ?? '—'}</td>
                      <td>
                        <span className={`badge bg-${getStatusBadgeClass(r.priority)}`}>
                          {formatStatusLabel(r.priority)}
                        </span>
                      </td>
                      <td>{formatCurrency(r.totalAmount)}</td>
                      <td>
                        <span className={`badge bg-${getStatusBadgeClass(r.status)}`}>
                          {formatStatusLabel(r.status)}
                        </span>
                      </td>
                      <td>{formatDate(r.createdAt)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

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
            Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} requests)
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

export default AdminPurchaseRequestsPage;
