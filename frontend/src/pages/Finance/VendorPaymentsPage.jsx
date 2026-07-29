import { useState, useEffect } from 'react';
import DataTable from '../../components/finance/DataTable';
import financeService from '../../services/financeService';
import { exportToCSV, exportToExcel } from '../../utils/exportUtils';

const VendorPaymentsPage = () => {
  const [vendorHistory, setVendorHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    financeService.getVendorPayments().then((data) => {
      if (isMounted) {
        setVendorHistory(data);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const totalPaidSum = vendorHistory.reduce((acc, curr) => acc + (curr.paidAmount || 0), 0);
  const totalPendingSum = vendorHistory.reduce((acc, curr) => acc + (curr.pendingAmount || 0), 0);

  const columns = [
    { header: 'Vendor Name', key: 'vendor', render: (r) => <span className="fw-bold text-dark">{r.vendor}</span> },
    { header: 'Total Invoices', key: 'totalInvoices' },
    {
      header: 'Total Volume',
      key: 'totalAmount',
      render: (r) => <span>${(r.totalAmount || 0).toLocaleString()}</span>,
    },
    {
      header: 'Paid Amount',
      key: 'paidAmount',
      render: (r) => <span className="fw-bold text-success">${(r.paidAmount || 0).toLocaleString()}</span>,
    },
    {
      header: 'Pending Amount',
      key: 'pendingAmount',
      render: (r) => (
        <span className={`fw-bold ${r.pendingAmount > 0 ? 'text-warning' : 'text-muted'}`}>
          ${(r.pendingAmount || 0).toLocaleString()}
        </span>
      ),
    },
    { header: 'Last Payment Date', key: 'lastPaymentDate' },
    {
      header: 'Status',
      key: 'status',
      render: (r) => {
        const badge =
          r.status === 'Clear'
            ? 'bg-success'
            : r.status === 'Active'
            ? 'bg-primary'
            : 'bg-warning text-dark';
        return <span className={`badge ${badge}`}>{r.status}</span>;
      },
    },
  ];

  return (
    <div className="finance-vendor-payments-page container-fluid py-3">
      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Vendor Payment History</h4>
          <p className="text-muted small mb-0">
            Consolidated overview of paid and pending financial disbursements per vendor.
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => exportToCSV(vendorHistory, 'vendor_payments.csv')}
          >
            <i className="bi bi-download me-1" /> Export CSV
          </button>
          <button
            className="btn btn-sm btn-outline-primary"
            onClick={() => exportToExcel(vendorHistory, 'vendor_payments.xls')}
          >
            <i className="bi bi-file-earmark-excel me-1" /> Export Excel
          </button>
        </div>
      </div>

      {/* Summary Metrics Cards */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-lg-4">
          <div className="card border-0 shadow-sm p-3 bg-primary text-white rounded-3">
            <div className="d-flex align-items-center justify-content-between">
              <div>
                <span className="small opacity-75 text-uppercase fw-semibold">Total Paid to Vendors</span>
                <h3 className="fw-bold mt-1 mb-0">${totalPaidSum.toLocaleString()}</h3>
              </div>
              <div className="bg-white bg-opacity-20 p-3 rounded-circle">
                <i className="bi bi-check-all fs-3" />
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-6 col-lg-4">
          <div className="card border-0 shadow-sm p-3 bg-warning text-dark rounded-3">
            <div className="d-flex align-items-center justify-content-between">
              <div>
                <span className="small opacity-75 text-uppercase fw-semibold">Total Pending Disbursements</span>
                <h3 className="fw-bold mt-1 mb-0">${totalPendingSum.toLocaleString()}</h3>
              </div>
              <div className="bg-dark bg-opacity-10 p-3 rounded-circle">
                <i className="bi bi-clock-history fs-3" />
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-6 col-lg-4">
          <div className="card border-0 shadow-sm p-3 bg-white rounded-3">
            <div className="d-flex align-items-center justify-content-between">
              <div>
                <span className="small text-muted text-uppercase fw-semibold">Total Vendors Onboarded</span>
                <h3 className="fw-bold text-dark mt-1 mb-0">{vendorHistory.length} Vendors</h3>
              </div>
              <div className="bg-light p-3 rounded-circle text-primary">
                <i className="bi bi-building fs-3" />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Table */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading vendor payment records...</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={vendorHistory}
          searchPlaceholder="Search vendor history..."
        />
      )}
    </div>
  );
};

export default VendorPaymentsPage;
