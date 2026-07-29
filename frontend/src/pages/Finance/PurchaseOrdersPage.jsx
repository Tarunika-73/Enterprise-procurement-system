import { useState, useEffect } from 'react';
import DataTable from '../../components/finance/DataTable';
import FinanceModal from '../../components/finance/FinanceModal';
import financeService from '../../services/financeService';
import { exportToCSV, exportToExcel, printDocument } from '../../utils/exportUtils';

const PurchaseOrdersPage = () => {
  const [purchaseOrders, setPurchaseOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedPO, setSelectedPO] = useState(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);

  useEffect(() => {
    let isMounted = true;
    financeService.getPurchaseOrders().then((data) => {
      if (isMounted) {
        setPurchaseOrders(data);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleApprove = (po) => {
    setPurchaseOrders((prev) =>
      prev.map((item) => (item.id === po.id ? { ...item, status: 'Approved' } : item))
    );
    showToast(`Purchase Order ${po.poNumber} Approved Successfully`);
  };

  const handleReject = (po) => {
    setPurchaseOrders((prev) =>
      prev.map((item) => (item.id === po.id ? { ...item, status: 'Rejected' } : item))
    );
    showToast(`Purchase Order ${po.poNumber} Rejected`);
  };

  const handleViewDetails = (po) => {
    setSelectedPO(po);
    setIsDetailModalOpen(true);
  };

  const handlePrintPO = (po) => {
    const html = `
      <div style="font-size: 14px; line-height: 1.6;">
        <p><strong>PO Number:</strong> ${po.poNumber}</p>
        <p><strong>Vendor:</strong> ${po.vendor} (${po.vendorEmail || 'N/A'})</p>
        <p><strong>Department:</strong> ${po.department}</p>
        <p><strong>Requisition #:</strong> ${po.requestNumber}</p>
        <p><strong>Order Date:</strong> ${po.orderDate} | <strong>Expected Delivery:</strong> ${po.expectedDelivery}</p>
        <p><strong>Status:</strong> ${po.status}</p>

        <h3>Itemized Breakdown</h3>
        <table>
          <thead>
            <tr>
              <th>Item / Product</th>
              <th>Qty</th>
              <th>Unit Price</th>
              <th>Tax</th>
              <th>Discount</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            ${(po.items || [])
              .map(
                (item) => `
              <tr>
                <td>${item.name}</td>
                <td>${item.quantity}</td>
                <td>$${item.price.toLocaleString()}</td>
                <td>$${item.tax || 0}</td>
                <td>$${item.discount || 0}</td>
                <td>$${(item.total || item.price * item.quantity).toLocaleString()}</td>
              </tr>
            `
              )
              .join('')}
          </tbody>
        </table>
        <h3 style="text-align: right; margin-top: 15px;">Grand Total: $${(po.totalAmount || 0).toLocaleString()}</h3>
      </div>
    `;
    printDocument(`Purchase Order ${po.poNumber}`, html);
  };

  const columns = [
    {
      header: 'PO Number',
      key: 'poNumber',
      render: (row) => (
        <span
          className="fw-bold text-primary"
          style={{ cursor: 'pointer' }}
          onClick={() => handleViewDetails(row)}
        >
          {row.poNumber}
        </span>
      ),
    },
    { header: 'Vendor', key: 'vendor' },
    { header: 'Department', key: 'department' },
    { header: 'Requisition #', key: 'requestNumber' },
    { header: 'Order Date', key: 'orderDate' },
    { header: 'Expected Delivery', key: 'expectedDelivery' },
    {
      header: 'Total Amount',
      key: 'totalAmount',
      render: (row) => <span className="fw-bold">${(row.totalAmount || 0).toLocaleString()}</span>,
    },
    {
      header: 'Status',
      key: 'status',
      render: (row) => {
        const badgeClass =
          row.status === 'Approved'
            ? 'bg-success'
            : row.status === 'Pending'
            ? 'bg-warning text-dark'
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
            onClick={() => handleViewDetails(row)}
          >
            <i className="bi bi-eye" />
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            title="Print / PDF"
            onClick={() => handlePrintPO(row)}
          >
            <i className="bi bi-printer" />
          </button>
          {row.status === 'Pending' && (
            <>
              <button
                type="button"
                className="btn btn-sm btn-success"
                title="Approve"
                onClick={() => handleApprove(row)}
              >
                <i className="bi bi-check-lg" />
              </button>
              <button
                type="button"
                className="btn btn-sm btn-danger"
                title="Reject"
                onClick={() => handleReject(row)}
              >
                <i className="bi bi-x-lg" />
              </button>
            </>
          )}
        </div>
      ),
    },
  ];

  const filterOptions = [
    { label: 'Approved', value: 'Approved' },
    { label: 'Pending', value: 'Pending' },
    { label: 'Rejected', value: 'Rejected' },
  ];

  return (
    <div className="finance-po-page container-fluid py-3">
      {/* Toast Alert */}
      {toastMessage && (
        <div className="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-info-circle me-2" />
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">Purchase Orders</h4>
          <p className="text-muted small mb-0">
            Review, approve, and track purchase orders across departments.
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-sm btn-outline-secondary"
            onClick={() => exportToCSV(purchaseOrders, 'purchase_orders.csv')}
          >
            <i className="bi bi-download me-1" /> Export CSV
          </button>
          <button
            className="btn btn-sm btn-outline-primary"
            onClick={() => exportToExcel(purchaseOrders, 'purchase_orders.xls')}
          >
            <i className="bi bi-file-earmark-excel me-1" /> Export Excel
          </button>
        </div>
      </div>

      {/* Table */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading purchase orders...</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={purchaseOrders}
          searchPlaceholder="Search by PO #, Vendor, Department..."
          filterKey="status"
          filterOptions={filterOptions}
        />
      )}

      {/* PO Detail Modal */}
      {selectedPO && (
        <FinanceModal
          isOpen={isDetailModalOpen}
          onClose={() => setIsDetailModalOpen(false)}
          title={`Purchase Order Details — ${selectedPO.poNumber}`}
          size="lg"
          footer={
            <div className="d-flex justify-content-between w-100">
              <button
                className="btn btn-outline-secondary"
                onClick={() => handlePrintPO(selectedPO)}
              >
                <i className="bi bi-printer me-1" /> Print / Download PDF
              </button>
              <button className="btn btn-primary" onClick={() => setIsDetailModalOpen(false)}>
                Close
              </button>
            </div>
          }
        >
          <div className="row g-3">
            {/* Vendor & General Details */}
            <div className="col-12 col-md-6">
              <div className="p-3 bg-light rounded">
                <h6 className="fw-bold text-primary mb-2">Vendor Details</h6>
                <p className="mb-1"><strong>Vendor:</strong> {selectedPO.vendor}</p>
                <p className="mb-1"><strong>Email:</strong> {selectedPO.vendorEmail || 'N/A'}</p>
                <p className="mb-0"><strong>Phone:</strong> {selectedPO.vendorPhone || 'N/A'}</p>
              </div>
            </div>

            <div className="col-12 col-md-6">
              <div className="p-3 bg-light rounded">
                <h6 className="fw-bold text-primary mb-2">Order Summary</h6>
                <p className="mb-1"><strong>Department:</strong> {selectedPO.department}</p>
                <p className="mb-1"><strong>Order Date:</strong> {selectedPO.orderDate}</p>
                <p className="mb-0"><strong>Expected Delivery:</strong> {selectedPO.expectedDelivery}</p>
              </div>
            </div>

            {/* Line Items */}
            <div className="col-12">
              <h6 className="fw-bold text-dark mt-2 mb-2">Line Items & Products</h6>
              <div className="table-responsive">
                <table className="table table-bordered table-sm align-middle">
                  <thead className="table-light">
                    <tr>
                      <th>Product</th>
                      <th>Quantity</th>
                      <th>Unit Price</th>
                      <th>Tax</th>
                      <th>Discount</th>
                      <th>Grand Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(selectedPO.items || []).map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.name}</td>
                        <td>{item.quantity}</td>
                        <td>${(item.price || 0).toLocaleString()}</td>
                        <td>${item.tax || 0}</td>
                        <td>${item.discount || 0}</td>
                        <td className="fw-bold">
                          ${(item.total || item.price * item.quantity).toLocaleString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Status Summary & Approval History */}
            <div className="col-12 col-md-6">
              <h6 className="fw-bold text-dark mb-2">Status Overview</h6>
              <ul className="list-group list-group-flush small">
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>PO Status:</span>
                  <span className="fw-bold">{selectedPO.status}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Delivery Status:</span>
                  <span>{selectedPO.deliveryStatus || 'Pending'}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Invoice Status:</span>
                  <span>{selectedPO.invoiceStatus || 'Uninvoiced'}</span>
                </li>
                <li className="list-group-item d-flex justify-content-between px-0">
                  <span>Payment Status:</span>
                  <span>{selectedPO.paymentStatus || 'Unpaid'}</span>
                </li>
              </ul>
            </div>

            <div className="col-12 col-md-6">
              <h6 className="fw-bold text-dark mb-2">Approval History</h6>
              <div className="timeline-list small">
                {(selectedPO.approvalHistory || []).map((app, idx) => (
                  <div key={idx} className="mb-2 p-2 border-start border-3 border-primary bg-light">
                    <div className="fw-bold text-dark">{app.step}</div>
                    <div className="text-muted">
                      Approver: {app.approver} | Date: {app.date}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </FinanceModal>
      )}
    </div>
  );
};

export default PurchaseOrdersPage;
