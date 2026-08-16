import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getFinanceDashboard } from '../../services/financeService';
import { formatCurrency } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';
import DashboardAnalytics from '../../components/dashboard/DashboardAnalytics';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';

const FinanceDashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getFinanceDashboard()
      .then((res) => setStats(res?.data ?? res))
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load dashboard.')))
      .finally(() => setLoading(false));
  }, []);

  const cards = [
    {
      icon: 'bi-hourglass-split',
      variant: 'warning',
      label: 'Pending Payments',
      value: loading ? '…' : (stats?.pendingPayments ?? 0),
      action: () => navigate('/finance/pending-payments'),
    },
    {
      icon: 'bi-check-circle',
      variant: 'success',
      label: 'Completed Payments',
      value: loading ? '…' : (stats?.completedPayments ?? 0),
      action: () => navigate('/finance/payment-history'),
    },
    {
      icon: 'bi-cash-stack',
      variant: 'danger',
      label: 'Pending Amount',
      value: loading ? '…' : formatCurrency(stats?.pendingAmount ?? 0),
      action: () => navigate('/finance/pending-payments'),
    },
    {
      icon: 'bi-wallet2',
      variant: 'primary',
      label: 'Total Amount Paid',
      value: loading ? '…' : formatCurrency(stats?.totalAmountPaid ?? 0),
      action: () => navigate('/finance/payment-history'),
    },
  ];

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Finance Dashboard</h1>
        <p className="text-muted mb-0">Process payments, review invoices, and manage financial reports.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row g-3 mb-4">
        {cards.map((card) => (
          <div key={card.label} className="col-12 col-sm-6 col-xl-3">
            <DashboardStatCard icon={card.icon} iconVariant={card.variant} value={card.value} label={card.label} onClick={card.action} />
          </div>
        ))}
      </div>

      <DashboardAnalytics />

      <div className="row g-3">
        <div className="col-12 col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="card-title fw-semibold mb-3">Quick Actions</h6>
              <div className="d-flex flex-column gap-2">
                <button
                  type="button"
                  className="btn btn-outline-warning text-start"
                  onClick={() => navigate('/finance/pending-payments')}
                >
                  <i className="bi bi-hourglass-split me-2" />
                  Review Pending Payments
                </button>
                <button
                  type="button"
                  className="btn btn-outline-primary text-start"
                  onClick={() => navigate('/finance/payment-history')}
                >
                  <i className="bi bi-clock-history me-2" />
                  View Payment History
                </button>
                <button
                  type="button"
                  className="btn btn-outline-secondary text-start"
                  onClick={() => navigate('/finance/reports')}
                >
                  <i className="bi bi-bar-chart-line me-2" />
                  Financial Reports
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="card-title fw-semibold mb-3">Finance Workflow</h6>
              <ol className="list-group list-group-numbered list-group-flush">
                {[
                  'Vendor delivers the order',
                  'Purchase Order status becomes DELIVERED',
                  'Finance Officer reviews and approves payment',
                  'Payment recorded with reference number',
                  'Purchase Order & Request marked CLOSED',
                ].map((step) => (
                  <li key={step} className="list-group-item border-0 px-0 py-1 small text-muted">
                    {step}
                  </li>
                ))}
              </ol>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default FinanceDashboard;
