import { useState, useEffect } from 'react';
import DataTable from '../../components/finance/DataTable';
import FinanceModal from '../../components/finance/FinanceModal';
import financeService from '../../services/financeService';
import { exportToCSV, exportToExcel, printDocument } from '../../utils/exportUtils';

const PAYMENT_METHODS = ['Bank Transfer', 'Cheque', 'UPI', 'Credit Card', 'Cash'];

const PaymentManagementPage = () => {
  const [payments, setPayments] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modals
  const [selectedPayment, setSelectedPayment] = useState(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Form State
  const [formData, setFormData] = useState({
    invoiceNumber: '',
    vendor: '',
    amount: '',
    paymentMethod: 'Bank Transfer',
    referenceNumber: '',
    paymentDate: new Date().toISOString().split('T')[0],
    remarks: '',
  });

  const [formErrors, setFormErrors] = useState({});
  const [toastMessage, setToastMessage] = useState(null);

  useEffect(() => {
    let isMounted = true;
    Promise.all([financeService.getPayments(), financeService.getInvoices()]).then(
      ([payData, invData]) => {
        if (isMounted) {
          setPayments(payData);
          setInvoices(invData);
          setLoading(false);
        }
      }
    );
    return () => {
      isMounted = false;
    };
  }, []);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const validateForm = () => {
    const errors = {};
    if (!formData.invoiceNumber) errors.invoiceNumber = 'Please select an invoice';
    if (!formData.referenceNumber.trim()) errors.referenceNumber = 'Reference Number is required';

    // Duplicate check
    const isDuplicate = payments.some(
      (p) =>
        p.referenceNumber.trim().toLowerCase() === formData.referenceNumber.trim().toLowerCase()
    );
    if (isDuplicate) {
      errors.referenceNumber = 'Reference Number already exists (Duplicate Payment)';
    }

    const selectedInv = invoices.find((i) => i.invoiceNumber === formData.invoiceNumber);
    if (!formData.amount || Number(formData.amount) <= 0) {
      errors.amount = 'Amount must be greater than 0';
    } else if (selectedInv && Number(formData.amount) > Number(selectedInv.total)) {
      errors.amount = `Payment Amount ($${formData.amount}) cannot exceed Invoice Total ($${selectedInv.total})`;
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const created = await financeService.createPayment(formData);
    setPayments((prev) => [created, ...prev]);

    // Update invoice payment status to paid
    await financeService.updateInvoice(formData.invoiceNumber, {
      status: 'Paid',
      paymentStatus: 'Paid',
    });

    setIsCreateModalOpen(false);
    showToast(`Payment ${created.paymentId} executed successfully!`);

    // Reset Form
    setFormData({
      invoiceNumber: '',
      vendor: '',
      amount: '',
      paymentMethod: 'Bank Transfer',
      referenceNumber: '',
      paymentDate: new Date().toISOString().split('T')[0],
      remarks: '',
    });
    setFormErrors({});
  };

  const handleDelete = async (payId) => {
    if (window.confirm('Are you sure you want to delete this payment record?')) {
      await financeService.deletePayment(payId);
      setPayments((prev) => prev.filter((p) => p.id !== payId));
      showToast('Payment record deleted');
    }
  };

  const handleDownloadReceipt = (pay) => {
    const html = `
      <div style="font-size: 14px; line-height: 1.6; padding: 10px;">
        <h2 style="color: #10b981;">PAYMENT RECEIPT</h2>
        <p><strong>Payment ID:</strong> ${pay.paymentId || pay.id}</p>
        <p><strong>Invoice Number:</strong> ${pay.invoiceNumber}</p>
        <p><strong>Vendor:</strong> ${pay.vendor}</p>
        <p><strong>Payment Date:</strong> ${pay.paymentDate}</p>
        <p><strong>Method:</strong> ${pay.paymentMethod}</p>
        <p><strong>Reference Number:</strong> ${pay.referenceNumber}</p>
        <p><strong>Status:</strong> ${pay.status}</p>
        <hr/>
        <h1 style="color: #1e3a8a;">Amount Paid: $${(pay.amount || 0).toLocaleString()}</h1>
        <p><em>Remarks: ${pay.remarks || 'None'}</em></p>
      </div>
    `;
    printDocument(`Receipt_${pay.paymentId}`, html);
  };

  const columns = [
    { header: 'Payment ID', key: 'paymentId' },
    { header: 'Invoice #', key: 'invoiceNumber' },
    { header: 'Vendor', key: 'vendor' },
    {
      header: 'Amount',
      key: 'amount',
      render: (row) => <span className="fw-bold text-success">${(row.amount || 0).toLocaleString()}</span>,
    },
    { header: 'Payment Date', key: 'paymentDate' },
    {
      header: 'Method',
      key: 'paymentMethod',
      render: (row) => <span className="badge bg-light text-dark border">{row.paymentMethod}</span>,
    },
    { header: 'Reference #', key: 'referenceNumber' },
    {
      header: 'Status',
      key: 'status',
      render: (row) => {
        const badgeClass =
          row.status === 'Completed'
            ? 'bg-success'
            : row.status === 'Scheduled'
            ? 'bg-info text-dark'
            : 'bg-danger';
        return <span className={`badge ${badgeClass}`}>{row.status}</span>;
      },
    },
    {
      header: 'Actions',
      key: 'actions',
      sortable: false,
      render: (row) => (
        <div className="d-flex align-items-center gap-1">
          <button
            type="button"
            className="btn btn-sm btn-outline-primary"
            title="View Details"
            onClick={() => {
              setSelectedPayment(row);
              setIsDetailModalOpen(true);
            }}
          >
            <i className="bi bi-eye" />
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-success"
            title="Download Receipt"
            onClick={() => handleDownloadReceipt(row)}
          >
            <i className="bi bi-receipt" />
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-danger"
            title="Delete Payment"
            onClick={() => handleDelete(row.id)}
          >
            <i className="bi bi-trash" />
          </button>
        </div>
      ),
    },
  ];

  const filterOptions = [
    { label: 'Completed', value: 'Completed' },
    { label: 'Scheduled', value: 'Scheduled' },
    { label: 'Failed', value: 'Failed' },
  ];

  return (
    <div className="finance-payment-page container-fluid py-3">
      {/* Toast */}
      {toastMessage && (
        <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-check-circle me-2" />
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Payment Management</h4>
          <p className="text-muted small mb-0">
            Execute, schedule, and track vendor disbursements and bank transfers.
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => exportToCSV(payments, 'payments.csv')}
          >
            <i className="bi bi-download me-1" /> Export CSV
          </button>
          <button
            className="btn btn-sm btn-outline-primary"
            onClick={() => exportToExcel(payments, 'payments.xls')}
          >
            <i className="bi bi-file-earmark-excel me-1" /> Export Excel
          </button>
          <button
            className="btn btn-sm btn-primary"
            onClick={() => setIsCreateModalOpen(true)}
          >
            <i className="bi bi-plus-lg me-1" /> Create Payment
          </button>
        </div>
      </div>

      {/* Table */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading payments list...</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={payments}
          searchPlaceholder="Search by Payment ID, Invoice #, Vendor, Reference #..."
          filterKey="status"
          filterOptions={filterOptions}
        />
      )}

      {/* Create Payment Modal */}
      <FinanceModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Record New Vendor Payment"
        size="md"
        footer={
          <div className="d-flex justify-content-end gap-2 w-100">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsCreateModalOpen(false)}
            >
              Cancel
            </button>
            <button type="button" className="btn btn-success" onClick={handleCreateSubmit}>
              Submit Payment
            </button>
          </div>
        }
      >
        <form onSubmit={handleCreateSubmit} className="row g-3">
          <div className="col-12">
            <label className="form-label fw-semibold small">Select Unpaid Invoice *</label>
            <select
              className={`form-select ${formErrors.invoiceNumber ? 'is-invalid' : ''}`}
              value={formData.invoiceNumber}
              onChange={(e) => {
                const invNum = e.target.value;
                const inv = invoices.find((i) => i.invoiceNumber === invNum);
                setFormData({
                  ...formData,
                  invoiceNumber: invNum,
                  vendor: inv ? inv.vendor : formData.vendor,
                  amount: inv ? inv.total : formData.amount,
                });
              }}
            >
              <option value="">-- Select Approved Invoice --</option>
              {invoices
                .filter((inv) => inv.paymentStatus !== 'Paid')
                .map((inv) => (
                  <option key={inv.id} value={inv.invoiceNumber}>
                    {inv.invoiceNumber} — {inv.vendor} (${(inv.total || 0).toLocaleString()})
                  </option>
                ))}
            </select>
            {formErrors.invoiceNumber && (
              <div className="invalid-feedback">{formErrors.invoiceNumber}</div>
            )}
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Vendor Name</label>
            <input
              type="text"
              className="form-control"
              value={formData.vendor}
              readOnly
              disabled
            />
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Payment Amount ($) *</label>
            <input
              type="number"
              className={`form-control ${formErrors.amount ? 'is-invalid' : ''}`}
              placeholder="0.00"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
            />
            {formErrors.amount && <div className="invalid-feedback">{formErrors.amount}</div>}
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Payment Method *</label>
            <select
              className="form-select"
              value={formData.paymentMethod}
              onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
            >
              {PAYMENT_METHODS.map((method) => (
                <option key={method} value={method}>
                  {method}
                </option>
              ))}
            </select>
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Reference Number *</label>
            <input
              type="text"
              className={`form-control ${formErrors.referenceNumber ? 'is-invalid' : ''}`}
              placeholder="e.g. TXN-8812903"
              value={formData.referenceNumber}
              onChange={(e) => setFormData({ ...formData, referenceNumber: e.target.value })}
            />
            {formErrors.referenceNumber && (
              <div className="invalid-feedback">{formErrors.referenceNumber}</div>
            )}
          </div>

          <div className="col-12">
            <label className="form-label fw-semibold small">Payment Date</label>
            <input
              type="date"
              className="form-control"
              value={formData.paymentDate}
              onChange={(e) => setFormData({ ...formData, paymentDate: e.target.value })}
            />
          </div>

          <div className="col-12">
            <label className="form-label fw-semibold small">Remarks / Transaction Note</label>
            <textarea
              className="form-control"
              rows="2"
              placeholder="Optional remarks..."
              value={formData.remarks}
              onChange={(e) => setFormData({ ...formData, remarks: e.target.value })}
            />
          </div>
        </form>
      </FinanceModal>

      {/* Detail Modal */}
      {selectedPayment && (
        <FinanceModal
          isOpen={isDetailModalOpen}
          onClose={() => setIsDetailModalOpen(false)}
          title={`Payment Record — ${selectedPayment.paymentId || selectedPayment.id}`}
          size="md"
          footer={
            <div className="d-flex justify-content-between w-100">
              <button
                className="btn btn-outline-success"
                onClick={() => handleDownloadReceipt(selectedPayment)}
              >
                <i className="bi bi-receipt me-1" /> Download Receipt
              </button>
              <button className="btn btn-primary" onClick={() => setIsDetailModalOpen(false)}>
                Close
              </button>
            </div>
          }
        >
          <div className="p-2">
            <ul className="list-group list-group-flush">
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Invoice Number:</span>
                <span className="fw-bold">{selectedPayment.invoiceNumber}</span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Vendor:</span>
                <span>{selectedPayment.vendor}</span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Payment Amount:</span>
                <span className="fw-bold text-success fs-5">
                  ${(selectedPayment.amount || 0).toLocaleString()}
                </span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Payment Method:</span>
                <span>{selectedPayment.paymentMethod}</span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Reference Number:</span>
                <span className="font-monospace">{selectedPayment.referenceNumber}</span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Payment Date:</span>
                <span>{selectedPayment.paymentDate}</span>
              </li>
              <li className="list-group-item d-flex justify-content-between px-0">
                <span>Remarks:</span>
                <span>{selectedPayment.remarks || 'None'}</span>
              </li>
            </ul>
          </div>
        </FinanceModal>
      )}
    </div>
  );
};

export default PaymentManagementPage;
