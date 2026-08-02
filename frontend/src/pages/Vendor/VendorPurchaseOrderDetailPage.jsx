import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import VendorStatusBadge from '../../components/vendor/VendorStatusBadge';
import {
  getVendorPurchaseOrderDetail,
  acceptVendorOrder,
  rejectVendorOrder,
} from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { formatCurrency, formatDate } from '../../utils/employeeHelpers';

const VendorPurchaseOrderDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [po, setPo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectRemarks, setRejectRemarks] = useState('');
  const [rejectError, setRejectError] = useState('');

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await getVendorPurchaseOrderDetail(id);
        if (mounted) setPo(res?.data ?? res);
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Unable to load purchase order.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, [id]);

  const handleAccept = async () => {
    setActionLoading(true);
    setActionError('');
    setActionSuccess('');
    try {
      const res = await acceptVendorOrder(id);
      setPo(res?.data ?? res);
      setActionSuccess('Order accepted successfully. Status updated to Accepted.');
    } catch (err) {
      setActionError(getApiErrorMessage(err, 'Failed to accept order.'));
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!rejectRemarks.trim()) {
      setRejectError('Remarks are required when rejecting an order.');
      return;
    }
    setActionLoading(true);
    setActionError('');
    setActionSuccess('');
    try {
      const res = await rejectVendorOrder(id, rejectRemarks);
      setPo(res?.data ?? res);
      setActionSuccess('Order rejected.');
      setShowRejectModal(false);
      setRejectRemarks('');
    } catch (err) {
      setActionError(getApiErrorMessage(err, 'Failed to reject order.'));
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return <div className="alert alert-danger" role="alert">{error}</div>;
  }

  if (!po) return null;

  const canAct = po.status === 'SENT';

  return (
    <>
      <div className="dashboard-page-header d-flex align-items-center gap-3">
        <button
          type="button"
          className="btn btn-sm btn-outline-secondary"
          onClick={() => navigate('/vendor/purchase-orders')}
        >
          <i className="bi bi-arrow-left me-1" />Back
        </button>
        <div>
          <h1>Purchase Order Details</h1>
          <p className="text-muted mb-0">{po.purchaseOrderNumber}</p>
        </div>
      </div>

      {actionError ? <div className="alert alert-danger">{actionError}</div> : null}
      {actionSuccess ? <div className="alert alert-success">{actionSuccess}</div> : null}

      <div className="row g-4">
        <div className="col-lg-6">
          <div className="employee-detail-card h-100">
            <h2 className="h6 fw-bold mb-3">Order Information</h2>
            <dl className="employee-detail-list">
              <div><dt>PO Number</dt><dd>{po.purchaseOrderNumber}</dd></div>
              <div><dt>Department</dt><dd>{po.departmentName || '—'}</dd></div>
              <div><dt>Procurement Officer</dt><dd>{po.procurementOfficerName || '—'}</dd></div>
              <div><dt>Delivery Address</dt><dd>{po.deliveryAddress || '—'}</dd></div>
              <div><dt>Delivery Date</dt><dd>{formatDate(po.expectedDeliveryDate)}</dd></div>
              <div><dt>Total Amount</dt><dd className="fw-semibold">{formatCurrency(po.totalAmount)}</dd></div>
              <div>
                <dt>Status</dt>
                <dd><VendorStatusBadge status={po.status} /></dd>
              </div>
            </dl>
          </div>
        </div>

        <div className="col-lg-6">
          <div className="employee-detail-card h-100">
            <h2 className="h6 fw-bold mb-3">Ordered Products</h2>
            {po.items?.length ? (
              <div className="table-responsive">
                <table className="table table-sm employee-table align-middle mb-0">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Qty</th>
                      <th>Unit Price</th>
                      <th>Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {po.items.map((item) => (
                      <tr key={item.id}>
                        <td>{item.productName}</td>
                        <td>{item.quantity}</td>
                        <td>{formatCurrency(item.unitPrice)}</td>
                        <td>{formatCurrency(item.totalPrice)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="text-muted">No items found.</p>
            )}
          </div>
        </div>
      </div>

      {canAct && (
        <div className="d-flex gap-2 mt-4 flex-wrap">
          <button
            type="button"
            className="btn btn-success"
            disabled={actionLoading}
            onClick={handleAccept}
          >
            {actionLoading ? <span className="spinner-border spinner-border-sm me-2" /> : null}
            Accept Order
          </button>
          <button
            type="button"
            className="btn btn-danger"
            disabled={actionLoading}
            onClick={() => { setShowRejectModal(true); setRejectError(''); }}
          >
            Reject Order
          </button>
          <button
            type="button"
            className="btn btn-outline-primary"
            onClick={() => navigate(`/vendor/deliveries?poId=${po.id}`)}
          >
            Update Delivery
          </button>
        </div>
      )}

      {!canAct && po.status === 'ACCEPTED' && (
        <div className="mt-4">
          <button
            type="button"
            className="btn btn-outline-primary"
            onClick={() => navigate(`/vendor/deliveries?poId=${po.id}`)}
          >
            <i className="bi bi-truck me-2" />Update Delivery
          </button>
        </div>
      )}

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="modal d-block" tabIndex="-1" style={{ background: 'rgba(0,0,0,0.4)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Reject Order</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setShowRejectModal(false)}
                  aria-label="Close"
                />
              </div>
              <div className="modal-body">
                <p className="text-muted small mb-3">
                  Please provide a reason for rejecting <strong>{po.purchaseOrderNumber}</strong>.
                </p>
                <label className="form-label fw-semibold" htmlFor="rejectRemarks">
                  Remarks <span className="text-danger">*</span>
                </label>
                <textarea
                  id="rejectRemarks"
                  className={`form-control ${rejectError ? 'is-invalid' : ''}`}
                  rows={3}
                  value={rejectRemarks}
                  onChange={(e) => { setRejectRemarks(e.target.value); setRejectError(''); }}
                  placeholder="Enter reason for rejection..."
                />
                {rejectError && <div className="invalid-feedback">{rejectError}</div>}
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowRejectModal(false)}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-danger"
                  disabled={actionLoading}
                  onClick={handleReject}
                >
                  {actionLoading ? <span className="spinner-border spinner-border-sm me-2" /> : null}
                  Confirm Reject
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default VendorPurchaseOrderDetailPage;
