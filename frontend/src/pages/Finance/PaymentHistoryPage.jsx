import { useCallback, useEffect, useState } from 'react';
import { getPaymentHistory } from '../../services/financeService';
import { formatCurrency, formatDate, formatStatusLabel, getPageContent, getPageMeta, getStatusBadgeClass } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const PaymentHistoryPage = () => {
  const [payments, setPayments] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getPaymentHistory({ page, size: 10 });
      const payload = res?.data ?? res;
      setPayments(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load payment history.'));
      setPayments([]);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const filtered = search.trim()
    ? payments.filter((p) =>
        [p.paymentReference, p.purchaseOrderNumber, p.vendorName, p.invoiceNumber]
          .some((v) => v?.toLowerCase().includes(search.toLowerCase()))
      )
    : payments;

  const statusBadge = (status) => (
    <span className={`badge bg-${getStatusBadgeClass(status)}`}>
      {formatStatusLabel(status)}
    </span>
  );

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Payment History</h1>
        <p className="text-muted mb-0">All processed payments.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="d-flex gap-3 mb-3 flex-wrap align-items-center">
        <input
          type="search"
          className="form-control form-control-sm"
          style={{ maxWidth: 280 }}
          placeholder="Search by reference, PO, vendor…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className="text-muted small ms-auto">
          {loading ? '' : `${meta.totalElements} payment(s)`}
        </span>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : filtered.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-inbox fs-1 d-block mb-2" />
              No payment records found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Reference</th>
                    <th>PO Number</th>
                    <th>Vendor</th>
                    <th>Invoice #</th>
                    <th>Amount</th>
                    <th>Paid Date</th>
                    <th>Method</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((p) => (
                    <tr key={p.id}>
                      <td className="fw-semibold font-monospace small">{p.paymentReference}</td>
                      <td>{p.purchaseOrderNumber}</td>
                      <td>{p.vendorName ?? '—'}</td>
                      <td>{p.invoiceNumber ?? '—'}</td>
                      <td className="fw-semibold">{formatCurrency(p.amountPaid)}</td>
                      <td>{formatDate(p.paymentDate)}</td>
                      <td>{p.paymentMethod ?? '—'}</td>
                      <td>{statusBadge(p.status)}</td>
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

export default PaymentHistoryPage;
