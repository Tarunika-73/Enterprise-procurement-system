import { useEffect, useState, useCallback } from 'react';
import {
  getAllPurchaseOrders,
  assignVendorToPO,
  sendPurchaseOrder,
  getActiveVendors,
} from '../services/procurementService';
import {
  getPageContent,
  getPageMeta,
  formatCurrency,
  formatDate,
  formatStatusLabel,
} from '../utils/employeeHelpers';
import { getApiErrorMessage } from '../utils/apiErrors';

const STATUS_CLASS = {
  CREATED: 'secondary',
  SENT: 'primary',
  ACCEPTED: 'info',
  IN_PROGRESS: 'warning',
  DELIVERED: 'success',
  CANCELLED: 'danger',
};

export default function PurchaseOrders() {
  const [orders, setOrders] = useState([]);
  const [meta, setMeta] = useState({ totalElements: 0, totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Assign vendor modal
  const [assignModal, setAssignModal] = useState(false);
  const [selectedPO, setSelectedPO] = useState(null);
  const [vendors, setVendors] = useState([]);
  const [vendorId, setVendorId] = useState('');
  const [assignError, setAssignError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Detail modal
  const [detailModal, setDetailModal] = useState(false);
  const [detailPO, setDetailPO] = useState(null);

  const load = useCallback((page = 0) => {
    setLoading(true);
    setError('');
    getAllPurchaseOrders({ page, size: 10 })
      .then((res) => {
        setOrders(getPageContent(res));
        setMeta(getPageMeta(res));
      })
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load purchase orders.')))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(0); }, [load]);

  const loadVendors = () => {
    if (vendors.length === 0) {
      getActiveVendors().then((res) => setVendors(getPageContent(res))).catch(() => {});
    }
  };

  const openAssign = (po) => {
    setSelectedPO(po);
    setVendorId(po.vendorId ? String(po.vendorId) : '');
    setAssignError('');
    setAssignModal(true);
    loadVendors();
  };

  const handleAssign = async () => {
    if (!vendorId) { setAssignError('Please select a vendor.'); return; }
    setSubmitting(true);
    setAssignError('');
    try {
      await assignVendorToPO(selectedPO.id, Number(vendorId));
      setAssignModal(false);
      load(meta.number);
    } catch (err) {
      setAssignError(getApiErrorMessage(err, 'Failed to assign vendor.'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleSend = async (po) => {
    if (!window.confirm(`Send PO ${po.purchaseOrderNumber} to vendor?`)) return;
    try {
      await sendPurchaseOrder(po.id);
      load(meta.number);
    } catch (err) {
      alert(getApiErrorMessage(err, 'Failed to send purchase order.'));
    }
  };

  return (
    <div className="container-fluid mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Purchase Orders</h2>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card shadow">
        <div className="card-header bg-success text-white d-flex justify-content-between align-items-center">
          <span>All Purchase Orders</span>
          <span className="badge bg-white text-success">{meta.totalElements} total</span>
        </div>
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-success" />
            </div>
          ) : orders.length === 0 ? (
            <div className="text-center py-5 text-muted">No purchase orders found.</div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>PO Number</th>
                    <th>Request #</th>
                    <th>Vendor</th>
                    <th>Status</th>
                    <th>Amount</th>
                    <th>Created Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((po) => (
                    <tr key={po.id}>
                      <td><code>{po.purchaseOrderNumber}</code></td>
                      <td><code>{po.requestNumber || '—'}</code></td>
                      <td>{po.vendorName || <span className="text-muted">Unassigned</span>}</td>
                      <td>
                        <span className={`badge bg-${STATUS_CLASS[po.status] || 'secondary'}`}>
                          {formatStatusLabel(po.status)}
                        </span>
                      </td>
                      <td>{formatCurrency(po.totalAmount)}</td>
                      <td>{formatDate(po.createdAt)}</td>
                      <td>
                        <button
                          className="btn btn-outline-primary btn-sm me-1"
                          onClick={() => { setDetailPO(po); setDetailModal(true); }}
                        >
                          View
                        </button>
                        {po.status === 'CREATED' && (
                          <>
                            <button
                              className="btn btn-outline-warning btn-sm me-1"
                              onClick={() => openAssign(po)}
                            >
                              Assign Vendor
                            </button>
                            {po.vendorId && (
                              <button
                                className="btn btn-success btn-sm"
                                onClick={() => handleSend(po)}
                              >
                                Send
                              </button>
                            )}
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        {meta.totalPages > 1 && (
          <div className="card-footer d-flex justify-content-between align-items-center">
            <small className="text-muted">Page {meta.number + 1} of {meta.totalPages}</small>
            <div className="btn-group btn-group-sm">
              <button
                className="btn btn-outline-secondary"
                disabled={meta.number === 0}
                onClick={() => load(meta.number - 1)}
              >
                Previous
              </button>
              <button
                className="btn btn-outline-secondary"
                disabled={meta.number + 1 >= meta.totalPages}
                onClick={() => load(meta.number + 1)}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Assign Vendor Modal */}
      {assignModal && (
        <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.4)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Assign Vendor — {selectedPO?.purchaseOrderNumber}</h5>
                <button className="btn-close" onClick={() => setAssignModal(false)} />
              </div>
              <div className="modal-body">
                {assignError && <div className="alert alert-danger py-2">{assignError}</div>}
                <div className="mb-3">
                  <label className="form-label fw-semibold">Vendor <span className="text-danger">*</span></label>
                  <select
                    className="form-select"
                    value={vendorId}
                    onChange={(e) => setVendorId(e.target.value)}
                  >
                    <option value="">— Select Vendor —</option>
                    {vendors.map((v) => (
                      <option key={v.id} value={v.id}>{v.vendorName}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setAssignModal(false)}>Cancel</button>
                <button className="btn btn-primary" onClick={handleAssign} disabled={submitting}>
                  {submitting ? <span className="spinner-border spinner-border-sm me-2" /> : null}
                  Assign Vendor
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Detail Modal */}
      {detailModal && detailPO && (
        <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.4)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">PO Details — {detailPO.purchaseOrderNumber}</h5>
                <button className="btn-close" onClick={() => setDetailModal(false)} />
              </div>
              <div className="modal-body">
                <dl className="row mb-0">
                  <dt className="col-5">PO Number</dt>
                  <dd className="col-7"><code>{detailPO.purchaseOrderNumber}</code></dd>
                  <dt className="col-5">Request #</dt>
                  <dd className="col-7"><code>{detailPO.requestNumber || '—'}</code></dd>
                  <dt className="col-5">Vendor</dt>
                  <dd className="col-7">{detailPO.vendorName || '—'}</dd>
                  <dt className="col-5">Vendor Email</dt>
                  <dd className="col-7">{detailPO.vendorEmail || '—'}</dd>
                  <dt className="col-5">Status</dt>
                  <dd className="col-7">
                    <span className={`badge bg-${STATUS_CLASS[detailPO.status] || 'secondary'}`}>
                      {formatStatusLabel(detailPO.status)}
                    </span>
                  </dd>
                  <dt className="col-5">Total Amount</dt>
                  <dd className="col-7">{formatCurrency(detailPO.totalAmount)}</dd>
                  <dt className="col-5">Expected Delivery</dt>
                  <dd className="col-7">{formatDate(detailPO.expectedDeliveryDate)}</dd>
                  <dt className="col-5">Created</dt>
                  <dd className="col-7">{formatDate(detailPO.createdAt)}</dd>
                </dl>
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setDetailModal(false)}>Close</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
