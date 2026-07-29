import { useState, useEffect } from 'react';
import DataTable from '../../components/finance/DataTable';
import FinanceModal from '../../components/finance/FinanceModal';
import financeService from '../../services/financeService';
import { exportToCSV, exportToExcel, printDocument } from '../../utils/exportUtils';

const InvoiceManagementPage = () => {
  const [invoices, setInvoices] = useState([]);
  const [purchaseOrders, setPurchaseOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modals state
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    invoiceNumber: '',
    vendor: '',
    purchaseOrder: '',
    invoiceDate: new Date().toISOString().split('T')[0],
    dueDate: '',
    amount: '',
    tax: '',
    discount: '',
    file: null,
  });

  const [formErrors, setFormErrors] = useState({});
  const [toastMessage, setToastMessage] = useState(null);

  useEffect(() => {
    let isMounted = true;
    Promise.all([financeService.getInvoices(), financeService.getPurchaseOrders()]).then(
      ([invData, poData]) => {
        if (isMounted) {
          setInvoices(invData);
          setPurchaseOrders(poData);
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

  // Form validation
  const validateForm = () => {
    const errors = {};
    if (!formData.invoiceNumber.trim()) {
      errors.invoiceNumber = 'Invoice Number is required';
    } else if (invoices.some((inv) => inv.invoiceNumber.toLowerCase() === formData.invoiceNumber.trim().toLowerCase())) {
      errors.invoiceNumber = 'Invoice Number must be unique';
    }

    if (!formData.vendor.trim()) errors.vendor = 'Vendor is required';
    if (!formData.purchaseOrder) errors.purchaseOrder = 'Select a Purchase Order';
    if (!formData.amount || Number(formData.amount) <= 0) errors.amount = 'Amount must be greater than 0';

    if (!formData.dueDate) {
      errors.dueDate = 'Due Date is required';
    } else if (new Date(formData.dueDate) < new Date(formData.invoiceDate)) {
      errors.dueDate = 'Due Date cannot be earlier than Invoice Date';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const created = await financeService.createInvoice(formData);
    setInvoices((prev) => [created, ...prev]);
    setIsCreateModalOpen(false);
    showToast(`Invoice ${created.invoiceNumber} created successfully!`);

    // Reset Form
    setFormData({
      invoiceNumber: '',
      vendor: '',
      purchaseOrder: '',
      invoiceDate: new Date().toISOString().split('T')[0],
      dueDate: '',
      amount: '',
      tax: '',
      discount: '',
      file: null,
    });
    setFormErrors({});
  };

  const handleApprove = (inv) => {
    setInvoices((prev) =>
      prev.map((item) => (item.id === inv.id ? { ...item, status: 'Approved' } : item))
    );
    showToast(`Invoice ${inv.invoiceNumber} Approved`);
  };

  const handleReject = (inv) => {
    setInvoices((prev) =>
      prev.map((item) => (item.id === inv.id ? { ...item, status: 'Rejected' } : item))
    );
    showToast(`Invoice ${inv.invoiceNumber} Rejected`);
  };

  const handleMarkPaid = (inv) => {
    setInvoices((prev) =>
      prev.map((item) =>
        item.id === inv.id ? { ...item, status: 'Paid', paymentStatus: 'Paid' } : item
      )
    );
    showToast(`Invoice ${inv.invoiceNumber} marked as Paid`);
  };

  const handlePrintInvoice = (inv) => {
    const html = `
      <div style="font-size: 14px; line-height: 1.6;">
        <h2>INVOICE: ${inv.invoiceNumber}</h2>
        <p><strong>Vendor:</strong> ${inv.vendor} (${inv.vendorAddress || 'N/A'})</p>
        <p><strong>Purchase Order #:</strong> ${inv.purchaseOrder}</p>
        <p><strong>Invoice Date:</strong> ${inv.invoiceDate} | <strong>Due Date:</strong> ${inv.dueDate}</p>
        <p><strong>Status:</strong> ${inv.status} (${inv.paymentStatus || 'Unpaid'})</p>
        <hr/>
        <h3>Summary</h3>
        <p>Subtotal: $${(inv.amount || 0).toLocaleString()}</p>
        <p>Taxes: $${(inv.tax || 0).toLocaleString()}</p>
        <p>Discounts: $${(inv.discount || 0).toLocaleString()}</p>
        <h2>Total Amount Due: $${(inv.total || 0).toLocaleString()}</h2>
      </div>
    `;
    printDocument(`Invoice ${inv.invoiceNumber}`, html);
  };

  const columns = [
    {
      header: 'Invoice #',
      key: 'invoiceNumber',
      render: (row) => (
        <span
          className="fw-bold text-primary"
          style={{ cursor: 'pointer' }}
          onClick={() => {
            setSelectedInvoice(row);
            setIsDetailModalOpen(true);
          }}
        >
          {row.invoiceNumber}
        </span>
      ),
    },
    { header: 'Vendor', key: 'vendor' },
    { header: 'Purchase Order', key: 'purchaseOrder' },
    { header: 'Invoice Date', key: 'invoiceDate' },
    { header: 'Due Date', key: 'dueDate' },
    {
      header: 'Amount',
      key: 'total',
      render: (row) => <span className="fw-bold">${(row.total || 0).toLocaleString()}</span>,
    },
    {
      header: 'Status',
      key: 'status',
      render: (row) => {
        const badgeClass =
          row.status === 'Paid'
            ? 'bg-success'
            : row.status === 'Approved'
            ? 'bg-primary'
            : row.status === 'Overdue'
            ? 'bg-danger'
            : row.status === 'Pending Approval'
            ? 'bg-warning text-dark'
            : 'bg-secondary';
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
              setSelectedInvoice(row);
              setIsDetailModalOpen(true);
            }}
          >
            <i className="bi bi-eye" />
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            title="Download / Print"
            onClick={() => handlePrintInvoice(row)}
          >
            <i className="bi bi-printer" />
          </button>

          {row.status === 'Pending Approval' && (
            <>
              <button
                type="button"
                className="btn btn-sm btn-success"
                title="Approve Invoice"
                onClick={() => handleApprove(row)}
              >
                <i className="bi bi-check-circle" />
              </button>
              <button
                type="button"
                className="btn btn-sm btn-danger"
                title="Reject Invoice"
                onClick={() => handleReject(row)}
              >
                <i className="bi bi-x-circle" />
              </button>
            </>
          )}

          {row.status === 'Approved' && row.paymentStatus !== 'Paid' && (
            <button
              type="button"
              className="btn btn-sm btn-outline-success"
              title="Mark as Paid"
              onClick={() => handleMarkPaid(row)}
            >
              <i className="bi bi-cash" /> Paid
            </button>
          )}
        </div>
      ),
    },
  ];

  const filterOptions = [
    { label: 'Paid', value: 'Paid' },
    { label: 'Approved', value: 'Approved' },
    { label: 'Pending Approval', value: 'Pending Approval' },
    { label: 'Overdue', value: 'Overdue' },
  ];

  const calculatedTotal =
    (Number(formData.amount) || 0) +
    (Number(formData.tax) || 0) -
    (Number(formData.discount) || 0);

  return (
    <div className="finance-invoice-page container-fluid py-3">
      {/* Toast Alert */}
      {toastMessage && (
        <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-check-circle me-2" />
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Invoice Management</h4>
          <p className="text-muted small mb-0">
            Process incoming vendor invoices, verify tax compliance, and approve payments.
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => exportToCSV(invoices, 'invoices.csv')}
          >
            <i className="bi bi-download me-1" /> Export CSV
          </button>
          <button
            className="btn btn-sm btn-outline-primary"
            onClick={() => exportToExcel(invoices, 'invoices.xls')}
          >
            <i className="bi bi-file-earmark-excel me-1" /> Export Excel
          </button>
          <button
            className="btn btn-sm btn-primary"
            onClick={() => setIsCreateModalOpen(true)}
          >
            <i className="bi bi-plus-lg me-1" /> Create Invoice
          </button>
        </div>
      </div>

      {/* Data Table */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading invoice records...</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={invoices}
          searchPlaceholder="Search by Invoice #, Vendor, PO #..."
          filterKey="status"
          filterOptions={filterOptions}
        />
      )}

      {/* Create Invoice Modal */}
      <FinanceModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Create New Vendor Invoice"
        size="lg"
        footer={
          <div className="d-flex justify-content-end gap-2 w-100">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsCreateModalOpen(false)}
            >
              Cancel
            </button>
            <button type="button" className="btn btn-primary" onClick={handleCreateSubmit}>
              Save Invoice
            </button>
          </div>
        }
      >
        <form onSubmit={handleCreateSubmit} className="row g-3">
          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Invoice Number *</label>
            <input
              type="text"
              className={`form-control ${formErrors.invoiceNumber ? 'is-invalid' : ''}`}
              placeholder="e.g. INV-2026-901"
              value={formData.invoiceNumber}
              onChange={(e) => setFormData({ ...formData, invoiceNumber: e.target.value })}
            />
            {formErrors.invoiceNumber && (
              <div className="invalid-feedback">{formErrors.invoiceNumber}</div>
            )}
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Select Purchase Order *</label>
            <select
              className={`form-select ${formErrors.purchaseOrder ? 'is-invalid' : ''}`}
              value={formData.purchaseOrder}
              onChange={(e) => {
                const poNumber = e.target.value;
                const po = purchaseOrders.find((p) => p.poNumber === poNumber);
                setFormData({
                  ...formData,
                  purchaseOrder: poNumber,
                  vendor: po ? po.vendor : formData.vendor,
                  amount: po ? po.totalAmount : formData.amount,
                });
              }}
            >
              <option value="">-- Choose Purchase Order --</option>
              {purchaseOrders.map((po) => (
                <option key={po.id} value={po.poNumber}>
                  {po.poNumber} — {po.vendor} (${(po.totalAmount || 0).toLocaleString()})
                </option>
              ))}
            </select>
            {formErrors.purchaseOrder && (
              <div className="invalid-feedback">{formErrors.purchaseOrder}</div>
            )}
          </div>

          <div className="col-12 col-md-6">
            <label className="form-label fw-semibold small">Vendor Name *</label>
            <input
              type="text"
              className={`form-control ${formErrors.vendor ? 'is-invalid' : ''}`}
              placeholder="e.g. TechCorp Solutions"
              value={formData.vendor}
              onChange={(e) => setFormData({ ...formData, vendor: e.target.value })}
            />
            {formErrors.vendor && <div className="invalid-feedback">{formErrors.vendor}</div>}
          </div>

          <div className="col-12 col-md-3">
            <label className="form-label fw-semibold small">Invoice Date *</label>
            <input
              type="date"
              className="form-control"
              value={formData.invoiceDate}
              onChange={(e) => setFormData({ ...formData, invoiceDate: e.target.value })}
            />
          </div>

          <div className="col-12 col-md-3">
            <label className="form-label fw-semibold small">Due Date *</label>
            <input
              type="date"
              className={`form-control ${formErrors.dueDate ? 'is-invalid' : ''}`}
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
            />
            {formErrors.dueDate && <div className="invalid-feedback">{formErrors.dueDate}</div>}
          </div>

          <div className="col-12 col-md-4">
            <label className="form-label fw-semibold small">Base Amount ($) *</label>
            <input
              type="number"
              className={`form-control ${formErrors.amount ? 'is-invalid' : ''}`}
              placeholder="0.00"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
            />
            {formErrors.amount && <div className="invalid-feedback">{formErrors.amount}</div>}
          </div>

          <div className="col-12 col-md-4">
            <label className="form-label fw-semibold small">Tax / GST ($)</label>
            <input
              type="number"
              className="form-control"
              placeholder="0.00"
              value={formData.tax}
              onChange={(e) => setFormData({ ...formData, tax: e.target.value })}
            />
          </div>

          <div className="col-12 col-md-4">
            <label className="form-label fw-semibold small">Discount ($)</label>
            <input
              type="number"
              className="form-control"
              placeholder="0.00"
              value={formData.discount}
              onChange={(e) => setFormData({ ...formData, discount: e.target.value })}
            />
          </div>

          <div className="col-12">
            <div className="p-3 bg-light rounded d-flex align-items-center justify-content-between">
              <span className="fw-semibold text-dark">Calculated Total Invoice Amount:</span>
              <span className="fs-5 fw-bold text-primary">${calculatedTotal.toLocaleString()}</span>
            </div>
          </div>

          <div className="col-12">
            <label className="form-label fw-semibold small">Upload Invoice PDF / Document</label>
            <input
              type="file"
              className="form-control"
              accept=".pdf,.png,.jpg,.jpeg"
              onChange={(e) => setFormData({ ...formData, file: e.target.files[0] })}
            />
          </div>
        </form>
      </FinanceModal>

      {/* Invoice Details Modal */}
      {selectedInvoice && (
        <FinanceModal
          isOpen={isDetailModalOpen}
          onClose={() => setIsDetailModalOpen(false)}
          title={`Invoice Details — ${selectedInvoice.invoiceNumber}`}
          size="lg"
          footer={
            <div className="d-flex justify-content-between w-100">
              <button
                className="btn btn-outline-secondary"
                onClick={() => handlePrintInvoice(selectedInvoice)}
              >
                <i className="bi bi-printer me-1" /> Print Invoice PDF
              </button>
              <button className="btn btn-primary" onClick={() => setIsDetailModalOpen(false)}>
                Close
              </button>
            </div>
          }
        >
          <div className="row g-3">
            <div className="col-12 col-md-6">
              <div className="p-3 bg-light rounded">
                <h6 className="fw-bold text-primary mb-2">Vendor Information</h6>
                <p className="mb-1"><strong>Vendor Name:</strong> {selectedInvoice.vendor}</p>
                <p className="mb-0"><strong>Address:</strong> {selectedInvoice.vendorAddress || 'N/A'}</p>
              </div>
            </div>

            <div className="col-12 col-md-6">
              <div className="p-3 bg-light rounded">
                <h6 className="fw-bold text-primary mb-2">Invoice Summary</h6>
                <p className="mb-1"><strong>PO #:</strong> {selectedInvoice.purchaseOrder}</p>
                <p className="mb-1"><strong>Invoice Date:</strong> {selectedInvoice.invoiceDate}</p>
                <p className="mb-0"><strong>Due Date:</strong> {selectedInvoice.dueDate}</p>
              </div>
            </div>

            {/* Line Items */}
            <div className="col-12">
              <h6 className="fw-bold text-dark mt-2 mb-2">Line Items Breakdown</h6>
              <div className="table-responsive">
                <table className="table table-bordered table-sm align-middle">
                  <thead className="table-light">
                    <tr>
                      <th>Description</th>
                      <th>Qty</th>
                      <th>Unit Price</th>
                      <th>Line Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(selectedInvoice.items || []).map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.description}</td>
                        <td>{item.qty}</td>
                        <td>${(item.unitPrice || 0).toLocaleString()}</td>
                        <td className="fw-bold">${(item.lineTotal || 0).toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Financial Breakdown */}
            <div className="col-12 col-md-6">
              <h6 className="fw-bold text-dark mb-2">Tax & Financial Summary</h6>
              <ul className="list-group list-group-flush small">
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Base Amount:</span>
                  <span>${(selectedInvoice.amount || 0).toLocaleString()}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Taxes ({selectedInvoice.gst || 'GST'}):</span>
                  <span>${(selectedInvoice.tax || 0).toLocaleString()}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Discount:</span>
                  <span>${(selectedInvoice.discount || 0).toLocaleString()}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0 fw-bold fs-6 text-primary">
                  <span>Total Amount Due:</span>
                  <span>${(selectedInvoice.total || 0).toLocaleString()}</span>
                </li>
              </ul>
            </div>

            <div className="col-12 col-md-6">
              <h6 className="fw-bold text-dark mb-2">Attachments</h6>
              {selectedInvoice.attachments && selectedInvoice.attachments.length > 0 ? (
                <ul className="list-group list-group-flush">
                  {selectedInvoice.attachments.map((att, idx) => (
                    <li key={idx} className="list-group-item d-flex align-items-center justify-content-between px-0">
                      <div className="d-flex align-items-center gap-2">
                        <i className="bi bi-file-earmark-pdf fs-4 text-danger" />
                        <div>
                          <div className="fw-bold small">{att.name}</div>
                          <div className="text-muted" style={{ fontSize: '0.75rem' }}>{att.size}</div>
                        </div>
                      </div>
                      <button className="btn btn-sm btn-outline-secondary" onClick={() => handlePrintInvoice(selectedInvoice)}>
                        <i className="bi bi-download" />
                      </button>
                    </li>
                  ))}
                </ul>
              ) : (
                <span className="text-muted small">No attachments uploaded</span>
              )}
            </div>
          </div>
        </FinanceModal>
      )}
    </div>
  );
};

export default InvoiceManagementPage;
