import { useEffect, useState } from 'react';
import { getPaymentHistory } from '../../services/financeService';
import { formatCurrency, formatStatusLabel, getStatusBadgeClass } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const FinanceReportsPage = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getPaymentHistory({ page: 0, size: 1000 })
      .then((res) => {
        const list = res?.data?.content ?? res?.content ?? [];
        setPayments(list.filter((p) => p.status === 'PAID'));
      })
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load report data.')))
      .finally(() => setLoading(false));
  }, []);

  // Monthly aggregation
  const monthly = payments.reduce((acc, p) => {
    const month = p.paymentDate ? p.paymentDate.substring(0, 7) : 'Unknown';
    acc[month] = (acc[month] || 0) + Number(p.amountPaid ?? 0);
    return acc;
  }, {});

  // Vendor-wise aggregation
  const vendorWise = payments.reduce((acc, p) => {
    const key = p.vendorName ?? 'Unknown';
    acc[key] = (acc[key] || 0) + Number(p.amountPaid ?? 0);
    return acc;
  }, {});

  const exportCSV = (rows, filename) => {
    const csv = rows.map((r) => r.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleMonthlyCSV = () => {
    const rows = [['Month', 'Total Paid (INR)'],
      ...Object.entries(monthly).sort().map(([m, v]) => [m, v.toFixed(2)])];
    exportCSV(rows, 'monthly_payments.csv');
  };

  const handleVendorCSV = () => {
    const rows = [['Vendor', 'Total Paid (INR)'],
      ...Object.entries(vendorWise).sort((a, b) => b[1] - a[1]).map(([v, a]) => [v, a.toFixed(2)])];
    exportCSV(rows, 'vendor_payments.csv');
  };

  const handleAllPaymentsCSV = () => {
    const rows = [
      ['Reference', 'PO Number', 'Vendor', 'Invoice', 'Amount', 'Date', 'Method', 'Status'],
      ...payments.map((p) => [
        p.paymentReference, p.purchaseOrderNumber, p.vendorName ?? '',
        p.invoiceNumber ?? '', p.amountPaid, p.paymentDate ?? '', p.paymentMethod ?? '', p.status,
      ]),
    ];
    exportCSV(rows, 'all_payments.csv');
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status" />
      </div>
    );
  }

  return (
    <>
      <div className="dashboard-page-header d-flex justify-content-between align-items-start flex-wrap gap-2">
        <div>
          <h1>Financial Reports</h1>
          <p className="text-muted mb-0">Spending summaries and payment analytics.</p>
        </div>
        <button type="button" className="btn btn-outline-success btn-sm" onClick={handleAllPaymentsCSV}>
          <i className="bi bi-download me-1" /> Export All (CSV)
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row g-3">
        {/* Monthly Payments */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-header bg-transparent d-flex justify-content-between align-items-center">
              <span className="fw-semibold">Monthly Payments</span>
              <button type="button" className="btn btn-outline-secondary btn-sm" onClick={handleMonthlyCSV}>
                <i className="bi bi-download me-1" /> CSV
              </button>
            </div>
            <div className="card-body p-0">
              {Object.keys(monthly).length === 0 ? (
                <p className="text-muted text-center py-4">No data available.</p>
              ) : (
                <div className="table-responsive">
                  <table className="table table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>Month</th>
                        <th className="text-end">Total Paid</th>
                      </tr>
                    </thead>
                    <tbody>
                      {Object.entries(monthly)
                        .sort((a, b) => b[0].localeCompare(a[0]))
                        .map(([month, total]) => (
                          <tr key={month}>
                            <td>{month}</td>
                            <td className="text-end fw-semibold">{formatCurrency(total)}</td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Vendor-wise Spending */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-header bg-transparent d-flex justify-content-between align-items-center">
              <span className="fw-semibold">Vendor-wise Spending</span>
              <button type="button" className="btn btn-outline-secondary btn-sm" onClick={handleVendorCSV}>
                <i className="bi bi-download me-1" /> CSV
              </button>
            </div>
            <div className="card-body p-0">
              {Object.keys(vendorWise).length === 0 ? (
                <p className="text-muted text-center py-4">No data available.</p>
              ) : (
                <div className="table-responsive">
                  <table className="table table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>Vendor</th>
                        <th className="text-end">Total Paid</th>
                      </tr>
                    </thead>
                    <tbody>
                      {Object.entries(vendorWise)
                        .sort((a, b) => b[1] - a[1])
                        .map(([vendor, total]) => (
                          <tr key={vendor}>
                            <td>{vendor}</td>
                            <td className="text-end fw-semibold">{formatCurrency(total)}</td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* All Payments Summary */}
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-header bg-transparent fw-semibold">All Payments</div>
            <div className="card-body p-0">
              {payments.length === 0 ? (
                <p className="text-muted text-center py-4">No completed payments found.</p>
              ) : (
                <div className="table-responsive">
                  <table className="table table-sm table-hover align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>Reference</th>
                        <th>PO Number</th>
                        <th>Vendor</th>
                        <th>Amount</th>
                        <th>Date</th>
                        <th>Method</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {payments.map((p) => (
                        <tr key={p.id}>
                          <td className="font-monospace small">{p.paymentReference}</td>
                          <td>{p.purchaseOrderNumber}</td>
                          <td>{p.vendorName ?? '—'}</td>
                          <td className="fw-semibold">{formatCurrency(p.amountPaid)}</td>
                          <td>{p.paymentDate ?? '—'}</td>
                          <td>{p.paymentMethod ?? '—'}</td>
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
        </div>
      </div>
    </>
  );
};

export default FinanceReportsPage;
