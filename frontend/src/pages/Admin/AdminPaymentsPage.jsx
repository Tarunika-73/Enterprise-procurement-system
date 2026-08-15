import { useCallback, useEffect, useState } from 'react';
import { getAdminPayments } from '../../services/adminService';
import {
  formatDate,
  formatCurrency,
  formatStatusLabel,
  getStatusBadgeClass,
  getPageContent,
  getPageMeta,
} from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminPaymentsPage = () => {
  const [payments, setPayments] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminPayments({ page, size: 10 });
      const payload = res?.data ?? res;
      setPayments(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load payments.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Payments</h1>
        <p className="text-muted mb-0">System-wide view of all payments.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : payments.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-currency-rupee fs-1 d-block mb-2" />
              No payments found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Payment Ref</th>
                    <th>Invoice #</th>
                    <th>Vendor</th>
                    <th>Amount Paid</th>
                    <th>Method</th>
                    <th>Payment Date</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {payments.map((p) => (
                    <tr key={p.id}>
                      <td className="fw-semibold">{p.paymentReference}</td>
                      <td>{p.invoiceNumber ?? '—'}</td>
                      <td>{p.vendorName ?? '—'}</td>
                      <td>{formatCurrency(p.amountPaid)}</td>
                      <td>{p.paymentMethod ?? '—'}</td>
                      <td>{formatDate(p.paymentDate)}</td>
                      <td>
                        <span className={`badge bg-${getStatusBadgeClass(p.status)}`}>
                          {formatStatusLabel(p.status)}
                        </span>
                      </td>
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
            Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} payments)
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

export default AdminPaymentsPage;
