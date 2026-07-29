import { useState, useEffect } from 'react';
import FinanceChart from '../../components/finance/FinanceChart';
import financeService from '../../services/financeService';
import { exportToCSV } from '../../utils/exportUtils';

const ExpenseDashboardPage = () => {
  const [expenseData, setExpenseData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedPeriod, setSelectedPeriod] = useState('monthly');

  useEffect(() => {
    let isMounted = true;
    financeService.getExpenseReports().then((data) => {
      if (isMounted) {
        setExpenseData(data);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const quarterlyData = [
    { label: 'Q1 (Jan-Mar)', value: 745000 },
    { label: 'Q2 (Apr-Jun)', value: 855400 },
    { label: 'Q3 (Jul-Sep)', value: 920000 },
    { label: 'Q4 (Oct-Dec)', value: 930000 },
  ];

  const yearlyData = [
    { label: '2024', value: 2400000 },
    { label: '2025', value: 3100000 },
    { label: '2026 (YTD)', value: 3450000 },
  ];

  const topExpenses = [
    { title: 'Cloud Infrastructure & Servers', category: 'IT', amount: 1250000, percentage: 36.2 },
    { title: 'Freight Logistics & Transit', category: 'Logistics', amount: 890000, percentage: 25.8 },
    { title: 'Marketing & Digital Campaigns', category: 'Marketing', amount: 540000, percentage: 15.6 },
    { title: 'Office Furniture & Equipment', category: 'Admin', amount: 450000, percentage: 13.0 },
    { title: 'Employee Workstations & Hardware', category: 'HR/IT', amount: 320000, percentage: 9.4 },
  ];

  return (
    <div className="finance-expense-dashboard container-fluid py-3">
      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Expense Analytics & Dashboard</h4>
          <p className="text-muted small mb-0">
            Interactive breakdown of procurement spend by department, vendor, and period.
          </p>
        </div>

        <div className="d-flex align-items-center gap-2">
          <select
            className="form-select form-select-sm"
            value={selectedPeriod}
            onChange={(e) => setSelectedPeriod(e.target.value)}
          >
            <option value="monthly">Monthly Spending</option>
            <option value="quarterly">Quarterly Spending</option>
            <option value="yearly">Yearly Spending</option>
          </select>
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => exportToCSV(topExpenses, 'top_expenses.csv')}
          >
            <i className="bi bi-download me-1" /> Export Data
          </button>
        </div>
      </div>

      {loading || !expenseData ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading expense charts...</p>
        </div>
      ) : (
        <div className="row g-4">
          {/* Main Spending Trend Chart */}
          <div className="col-12 col-lg-8">
            <div className="card border-0 shadow-sm p-3 h-100">
              <FinanceChart
                type="area"
                title={`Expense Trend (${selectedPeriod.toUpperCase()})`}
                data={
                  selectedPeriod === 'monthly'
                    ? expenseData.monthly
                    : selectedPeriod === 'quarterly'
                    ? quarterlyData
                    : yearlyData
                }
                color="#2563eb"
                height={280}
              />
            </div>
          </div>

          {/* Department Distribution Pie Chart */}
          <div className="col-12 col-lg-4">
            <div className="card border-0 shadow-sm p-3 h-100">
              <FinanceChart
                type="pie"
                title="Department Wise Spending"
                data={expenseData.department}
                height={280}
              />
            </div>
          </div>

          {/* Vendor Wise Spending Bar Chart */}
          <div className="col-12 col-lg-6">
            <div className="card border-0 shadow-sm p-3 h-100">
              <FinanceChart
                type="bar"
                title="Top Vendor Wise Spending"
                data={expenseData.vendor}
                color="#10b981"
                height={260}
              />
            </div>
          </div>

          {/* Top Expenses Highlights List */}
          <div className="col-12 col-lg-6">
            <div className="card border-0 shadow-sm p-3 h-100">
              <h6 className="fw-semibold text-dark mb-3">Top Procurement Expenses</h6>
              <div className="list-group list-group-flush">
                {topExpenses.map((item, idx) => (
                  <div key={idx} className="list-group-item px-0 border-0 py-2">
                    <div className="d-flex align-items-center justify-content-between mb-1">
                      <span className="fw-semibold text-dark small">{item.title}</span>
                      <span className="fw-bold text-primary small">${item.amount.toLocaleString()}</span>
                    </div>
                    <div className="progress" style={{ height: '6px' }}>
                      <div
                        className="progress-bar bg-primary"
                        role="progressbar"
                        style={{ width: `${item.percentage}%` }}
                        aria-valuenow={item.percentage}
                        aria-valuemin="0"
                        aria-valuemax="100"
                      />
                    </div>
                    <div className="d-flex justify-content-between small text-muted mt-1" style={{ fontSize: '0.75rem' }}>
                      <span>Category: {item.category}</span>
                      <span>{item.percentage}% of total</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ExpenseDashboardPage;
