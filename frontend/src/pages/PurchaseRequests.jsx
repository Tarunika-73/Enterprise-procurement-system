import { useEffect, useState, useCallback } from 'react';
import {
  getApprovedPurchaseRequests,
  createPurchaseOrder,
  getActiveVendors,
} from '../services/procurementService';
import {
  getPageContent,
  getPageMeta,
  formatCurrency,
  formatDate,
  getStatusBadgeClass,
  formatStatusLabel,
} from '../utils/employeeHelpers';
import { getApiErrorMessage } from '../utils/apiErrors';

const PRIORITY_CLASS = { HIGH: 'danger', URGENT: 'danger', MEDIUM: 'warning', NORMAL: 'secondary' };

export default function PurchaseRequests() {
  const [requests, setRequests] = useState([]);
  const [meta, setMeta] = useState({ totalElements: 0, totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [selected, setSelected] = useState(null);
  const [vendors, setVendors] = useState([]);
  const [vendorId, setVendorId] = useState('');
  const [deliveryDate, setDeliveryDate] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [modalError, setModalError] = useState('');
  const [createdPOs, setCreatedPOs] = useState(new Set());

  const load = useCallback((page = 0) => {
    setLoading(true);
    setError('');
    getApprovedPurchaseRequests({ page, size: 10 })
      .then((res) => {
        setRequests(getPageContent(res));
        setMeta(getPageMeta(res));
      })
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load approved requests.')))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(0); }, [load]);

  const openModal = (req) => {
    setSelected(req);
    setVendorId('');
    setDeliveryDate('');
    setModalError('');
    setShowModal(true);
    if (vendors.length === 0) {
      getActiveVendors().then((res) => setVendors(getPageContent(res))).catch(() => {});
    }
  };

  const handleCreate = async () => {
    if (!vendorId) { setModalError('Please select a vendor.'); return; }
    setSubmitting(true);
    setModalError('');
    try {
      await createPurchaseOrder({
        purchaseRequestId: selected.id,
        vendorId: Number(vendorId),
        expectedDeliveryDate: deliveryDate || null,
      });
      setCreatedPOs((prev) => new Set(prev).add(selected.id));
      setShowModal(false);
    } catch (err) {
      setModalError(getApiErrorMessage(err, 'Failed to create Purchase Order.'));
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = requests.filter((r) => {
    const q = search.toLowerCase();
    return (
      !q ||
      r.requestNumber?.toLowerCase().includes(q) ||
      r.requesterName?.toLowerCase().includes(q) ||
      r.departmentName?.toLowerCase().includes(q) ||
      r.productName?.toLowerCase().includes(q)
    );
  });

  return (
    <div className="container-fluid mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Purchase Requests</h2>
        <input
          type="text"
          className="form-control"
          placeholder="Search by request, employee, department, product…"
          style={{ width: 320 }}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card shadow">
        <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <span>Approved Purchase Requests</span>
          <span className="badge bg-white text-primary">{meta.totalElements} total</span>
        </div>
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" />
            </div>
          ) : filtered.length === 0 ? (
            <div className="text-center py-5 text-muted">No approved requests found.</div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Request #</th>
                    <th>Employee</th>
                    <th>Department</th>
                    <th>Product</th>
                    <th>Qty</th>
                    <th>Total Amount</th>
                    <th>Priority</th>
                    <th>Approved Date</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((r) => {
                    const hasPO = createdPOs.has(r.id);
                    return (
                      <tr key={r.id}>
                        <td><code>{r.requestNumber}</code></td>
                        <td>{r.requesterName || '—'}</td>
                        <td>{r.departmentName || '—'}</td>
                        <td>{r.productName || r.title || '—'}</td>
                        <td>{r.quantity ?? '—'}</td>
                        <td>{formatCurrency(r.totalAmount)}</td>
                        <td>
                          <span className={`badge bg-${PRIORITY_CLASS[r.priority] || 'secondary'}`}>
                            {formatStatusLabel(r.priority)}
                          </span>
                        </td>
                        <td>{formatDate(r.approvalDate)}</td>
                        <td>
                          {hasPO ? (
                            <span className="badge bg-success">PO Created</span>
                          ) : (
                            <button
                              className="btn btn-success btn-sm"
                              onClick={() => openModal(r)}
                            >
                              Create PO
                            </button>
                          )}
                        </td>
                      </tr>
                    );
                  })}
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

      {/* Create PO Modal */}
      {showModal && (
        <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.4)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Create Purchase Order</h5>
                <button className="btn-close" onClick={() => setShowModal(false)} />
              </div>
              <div className="modal-body">
                {modalError && <div className="alert alert-danger py-2">{modalError}</div>}
                <p className="text-muted mb-3">
                  Request: <strong>{selected?.requestNumber}</strong> &mdash; {selected?.productName || selected?.title}
                </p>
                <div className="mb-3">
                  <label className="form-label fw-semibold">Assign Vendor <span className="text-danger">*</span></label>
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
                <div className="mb-3">
                  <label className="form-label fw-semibold">Expected Delivery Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={deliveryDate}
                    onChange={(e) => setDeliveryDate(e.target.value)}
                    min={new Date().toISOString().split('T')[0]}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button
                  className="btn btn-success"
                  onClick={handleCreate}
                  disabled={submitting}
                >
                  {submitting ? <span className="spinner-border spinner-border-sm me-2" /> : null}
                  Create Purchase Order
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
