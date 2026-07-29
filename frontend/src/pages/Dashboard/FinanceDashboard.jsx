import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import FinanceChart from '../../components/finance/FinanceChart';
import financeService from '../../services/financeService';
import { exportToExcel } from '../../utils/exportUtils';

const FinanceDashboard = () => {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    financeService.getDashboardMetrics().then((res) => {
      if (isMounted) {
        setData(res);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  if (loading || !data) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status" />
        <p className="mt-2 text-muted">Loading Finance Executive Dashboard...</p>
      </div>
    );
  }

  const { stats, monthlySpending, vendorSpending, departmentSpending, invoiceStatus } = data;

  const statCardsList = [
    { label: 'Total Purchase Orders', value: stats.totalPurchaseOrders, icon: 'bi-cart-check', color: 'primary' },
    { label: 'Pending POs', value: stats.pendingPurchaseOrders, icon: 'bi-clock-history', color: 'warning' },
    { label: 'Total Invoices', value: stats.totalInvoices, icon: 'bi-receipt', color: 'info' },
    { label: 'Pending Invoices', value: stats.pendingInvoices, icon: 'bi-hourglass-split', color: 'warning' },
    { label: 'Paid Invoices', value: stats.paidInvoices, icon: 'bi-check2-circle', color: 'success' },
    { label: 'Pending Payments', value: stats.pendingPayments, icon: 'bi-currency-dollar', color: 'danger' },
    { label: 'Total Expenses', value: stats.totalExpenses, icon: 'bi-wallet2', color: 'primary' },
    { label: 'Monthly Expenses', value: stats.monthlyExpenses, icon: 'bi-graph-up-arrow', color: 'success' },
    { label: 'Total Vendors', value: stats.totalVendors, icon: 'bi-building', color: 'secondary' },
    { label: 'Overdue Payments', value: stats.overduePayments, icon: 'bi-exclamation-triangle', color: 'danger' },
  ];

  return (
    <div className="finance-dashboard-page container-fluid py-3">
      {/* Executive Header & Quick Actions */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Finance Executive Dashboard</h4>
          <p className="text-muted small mb-0">
            Overview of financial procurement operations, invoice approvals, and corporate disbursements.
          </p>
        </div>

        {/* Quick Actions per document.md */}
        <div className="d-flex flex-wrap gap-2">
          <button
            className="btn btn-sm btn-success"
            onClick={() => navigate('/dashboard/finance/payments')}
          >
            <i className="bi bi-credit-card me-1" /> Create Payment
          </button>
          <button
            className="btn btn-sm btn-outline-primary"
            onClick={() => navigate('/dashboard/finance/invoices')}
          >
            <i className="bi bi-receipt me-1" /> View Invoices
          </button>
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => navigate('/dashboard/finance/reports')}
          >
            <i className="bi bi-bar-chart-line me-1" /> Generate Report
          </button>
          <button
            className="btn btn-sm btn-outline-dark"
            onClick={() => exportToExcel(monthlySpending, 'finance_dashboard_summary.xls')}
          >
            <i className="bi bi-file-earmark-excel me-1" /> Export Excel
          </button>
        </div>
      </div>

      {/* 10 Summary Cards Grid */}
      <div className="row g-3 mb-4">
        {statCardsList.map((card, index) => (
          <div key={index} className="col-12 col-sm-6 col-md-4 col-lg-2-4">
            <div className="card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
              <div className="d-flex align-items-center justify-content-between mb-2">
                <span className="small text-muted fw-medium">{card.label}</span>
                <div className={`p-2 rounded-circle bg-${card.color} bg-opacity-10 text-${card.color}`}>
                  <i className={`bi ${card.icon} fs-5`} />
                </div>
              </div>
              <h4 className="fw-bold text-dark mb-0">{card.value}</h4>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Grid */}
      <div className="row g-4 mb-4">
        {/* Monthly Spending */}
        <div className="col-12 col-lg-8">
          <div className="card border-0 shadow-sm p-3 h-100">
            <FinanceChart
              type="area"
              title="Monthly Spend Analysis (2026)"
              data={monthlySpending}
              color="#2563eb"
              height={260}
            />
          </div>
        </div>

        {/* Invoice Status Distribution */}
        <div className="col-12 col-lg-4">
          <div className="card border-0 shadow-sm p-3 h-100">
            <FinanceChart
              type="pie"
              title="Invoice Status Breakdown"
              data={invoiceStatus}
              height={260}
            />
          </div>
        </div>

        {/* Vendor Wise Spending */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm p-3 h-100">
            <FinanceChart
              type="bar"
              title="Vendor Wise Spending"
              data={vendorSpending}
              color="#10b981"
              height={250}
            />
          </div>
        </div>

        {/* Department Wise Spending */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm p-3 h-100">
            <FinanceChart
              type="bar"
              title="Department Wise Spending"
              data={departmentSpending}
              color="#8b5cf6"
              height={250}
            />
          </div>
        </div>
      </div>

      {/* Recent Activity Sections */}
      <div className="row g-4">
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm p-3">
            <div className="d-flex align-items-center justify-content-between mb-3">
              <h6 className="fw-bold text-dark mb-0">Latest Invoices</h6>
              <button
                className="btn btn-link btn-sm text-decoration-none"
                onClick={() => navigate('/dashboard/finance/invoices')}
              >
                View All →
              </button>
            </div>
            <ul className="list-group list-group-flush small">
              <li className="list-group-item d-flex justify-content-between align-items-center px-0">
                <div>
                  <span className="fw-bold d-block text-dark">INV-2026-101</span>
                  <span className="text-muted">TechCorp Solutions</span>
                </div>
                <span className="fw-bold text-dark">$45,000</span>
              </li>
              <li className="list-group-item d-flex justify-content-between align-items-center px-0">
                <div>
                  <span className="fw-bold d-block text-dark">INV-2026-104</span>
                  <span className="text-muted">Global Logistics Inc</span>
                </div>
                <span className="fw-bold text-dark">$18,500</span>
              </li>
            </ul>
          </div>
        </div>

        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm p-3">
            <div className="d-flex align-items-center justify-content-between mb-3">
              <h6 className="fw-bold text-dark mb-0">Recent Purchase Orders</h6>
              <button
                className="btn btn-link btn-sm text-decoration-none"
                onClick={() => navigate('/dashboard/finance/purchase-orders')}
              >
                View All →
              </button>
            </div>
            <ul className="list-group list-group-flush small">
              <li className="list-group-item d-flex justify-content-between align-items-center px-0">
                <div>
                  <span className="fw-bold d-block text-dark">PO-2026-001</span>
                  <span className="text-muted">IT & Infrastructure</span>
                </div>
                <span className="badge bg-success">Approved</span>
              </li>
              <li className="list-group-item d-flex justify-content-between align-items-center px-0">
                <div>
                  <span className="fw-bold d-block text-dark">PO-2026-002</span>
                  <span className="text-muted">Operations & Logistics</span>
                </div>
                <span className="badge bg-warning text-dark">Pending</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FinanceDashboard;
