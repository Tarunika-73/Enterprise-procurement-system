import { useState } from 'react';
import { exportToCSV, exportToExcel, printDocument } from '../../utils/exportUtils';
import {
  MOCK_MONTHLY_SPENDING,
  MOCK_VENDOR_SPENDING,
  MOCK_DEPARTMENT_SPENDING,
  MOCK_INVOICES,
  MOCK_PAYMENTS,
} from '../../utils/mockFinanceData';

const REPORT_TYPES = [
  { id: 'monthly', title: 'Monthly Expense Report', icon: 'bi-calendar-month' },
  { id: 'quarterly', title: 'Quarterly Financial Summary', icon: 'bi-calendar3' },
  { id: 'annual', title: 'Annual Procurement Report', icon: 'bi-journal-bookmark-fill' },
  { id: 'vendor', title: 'Vendor Spend Breakdown', icon: 'bi-building' },
  { id: 'department', title: 'Department Spending Report', icon: 'bi-diagram-3' },
  { id: 'payment', title: 'Payment Audit & History Report', icon: 'bi-cash-stack' },
  { id: 'invoice', title: 'Invoice Status & Tax Compliance Report', icon: 'bi-receipt' },
];

const FinancialReportsPage = () => {
  const [selectedType, setSelectedType] = useState('monthly');
  const [dateRange, setDateRange] = useState({ start: '2026-01-01', end: '2026-12-31' });

  const getReportData = () => {
    switch (selectedType) {
      case 'monthly':
        return MOCK_MONTHLY_SPENDING;
      case 'vendor':
        return MOCK_VENDOR_SPENDING;
      case 'department':
        return MOCK_DEPARTMENT_SPENDING;
      case 'invoice':
        return MOCK_INVOICES.map((i) => ({
          InvoiceNumber: i.invoiceNumber,
          Vendor: i.vendor,
          Date: i.invoiceDate,
          Amount: i.total,
          Status: i.status,
        }));
      case 'payment':
        return MOCK_PAYMENTS.map((p) => ({
          PaymentID: p.paymentId,
          Invoice: p.invoiceNumber,
          Vendor: p.vendor,
          Amount: p.amount,
          Method: p.paymentMethod,
          Ref: p.referenceNumber,
          Status: p.status,
        }));
      default:
        return MOCK_MONTHLY_SPENDING;
    }
  };

  const handlePrint = () => {
    const reportName = REPORT_TYPES.find((r) => r.id === selectedType)?.title || 'Financial Report';
    const data = getReportData();
    if (!data || !data.length) return;

    const keys = Object.keys(data[0]);
    const html = `
      <div>
        <h3>Report Type: ${reportName}</h3>
        <p><strong>Date Filter:</strong> ${dateRange.start} to ${dateRange.end}</p>
        <table>
          <thead>
            <tr>${keys.map((k) => `<th>${k}</th>`).join('')}</tr>
          </thead>
          <tbody>
            ${data
              .map(
                (row) =>
                  `<tr>${keys.map((k) => `<td>${row[k] ?? ''}</td>`).join('')}</tr>`
              )
              .join('')}
          </tbody>
        </table>
      </div>
    `;
    printDocument(reportName, html);
  };

  const currentReportTitle = REPORT_TYPES.find((r) => r.id === selectedType)?.title;
  const currentData = getReportData();

  return (
    <div className="finance-reports-page container-fluid py-3">
      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Financial Report Generator</h4>
          <p className="text-muted small mb-0">
            Generate, customize, and export executive financial summaries and tax reports.
          </p>
        </div>
      </div>

      <div className="row g-4">
        {/* Sidebar Selection Menu */}
        <div className="col-12 col-lg-4">
          <div className="card border-0 shadow-sm p-3">
            <h6 className="fw-semibold text-dark mb-3">Select Report Type</h6>
            <div className="list-group list-group-flush">
              {REPORT_TYPES.map((rep) => (
                <button
                  key={rep.id}
                  type="button"
                  className={`list-group-item list-group-item-action d-flex align-items-center gap-3 py-3 border-0 rounded mb-1 ${
                    selectedType === rep.id ? 'bg-primary text-white' : 'text-dark'
                  }`}
                  onClick={() => setSelectedType(rep.id)}
                >
                  <i className={`bi ${rep.icon} fs-5`} />
                  <span className="fw-medium small">{rep.title}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Report Preview & Controls */}
        <div className="col-12 col-lg-8">
          <div className="card border-0 shadow-sm p-4">
            <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-3 pb-3 border-bottom">
              <div>
                <h5 className="fw-bold text-dark mb-1">{currentReportTitle}</h5>
                <span className="badge bg-light text-primary border">Ready for Export</span>
              </div>

              {/* Date Filters */}
              <div className="d-flex align-items-center gap-2">
                <input
                  type="date"
                  className="form-control form-control-sm"
                  value={dateRange.start}
                  onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                />
                <span className="text-muted small">to</span>
                <input
                  type="date"
                  className="form-control form-control-sm"
                  value={dateRange.end}
                  onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                />
              </div>
            </div>

            {/* Export Buttons */}
            <div className="d-flex flex-wrap gap-2 mb-4">
              <button className="btn btn-sm btn-outline-secondary" onClick={handlePrint}>
                <i className="bi bi-printer me-1" /> PDF / Print
              </button>
              <button
                className="btn btn-sm btn-outline-success"
                onClick={() => exportToCSV(currentData, `${selectedType}_report.csv`)}
              >
                <i className="bi bi-file-earmark-code me-1" /> Export CSV
              </button>
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => exportToExcel(currentData, `${selectedType}_report.xls`)}
              >
                <i className="bi bi-file-earmark-excel me-1" /> Export Excel
              </button>
            </div>

            {/* Data Preview Table */}
            <h6 className="fw-semibold text-muted small uppercase mb-2">Report Preview Dataset</h6>
            <div className="table-responsive bg-light rounded p-2">
              {currentData && currentData.length > 0 ? (
                <table className="table table-sm table-striped align-middle mb-0">
                  <thead>
                    <tr>
                      {Object.keys(currentData[0]).map((key) => (
                        <th key={key} className="text-capitalize small fw-bold">
                          {key}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {currentData.map((row, i) => (
                      <tr key={i}>
                        {Object.keys(row).map((key) => (
                          <td key={key} className="small">
                            {typeof row[key] === 'number' ? `$${row[key].toLocaleString()}` : row[key]}
                          </td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div className="text-center py-4 text-muted">No report data generated</div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FinancialReportsPage;
