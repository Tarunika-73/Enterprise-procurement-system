import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import VendorStatusBadge from '../../components/vendor/VendorStatusBadge';
import {
  getVendorPurchaseOrders,
  getVendorPurchaseOrderDetail,
  updateVendorDelivery,
} from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { formatCurrency, formatDate, getPageContent } from '../../utils/employeeHelpers';

const DELIVERY_STATUSES = [
  { value: 'PENDING', label: 'Preparing' },
  { value: 'IN_TRANSIT', label: 'In Transit' },
  { value: 'DELIVERED', label: 'Delivered' },
];

const VendorUpdateDeliveryPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const preselectedPoId = searchParams.get('poId');

  const [orders, setOrders] = useState([]);
  const [selectedPoId, setSelectedPoId] = useState(preselectedPoId || '');
  const [po, setPo] = useState(null);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [loadingPo, setLoadingPo] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  const [form, setForm] = useState({
    deliveryStatus: 'PENDING',
    dispatchNumber: '',
    dispatchDate: '',
    expectedDeliveryDate: '',
    remarks: '',
  });

  // Load accepted orders for the dropdown
  useEffect(() => {
    const loadOrders = async () => {
      setLoadingOrders(true);
      try {
        // Fetch all non-terminal orders eligible for delivery updates (ACCEPTED + IN_TRANSIT)
        const [acceptedRes, inTransitRes] = await Promise.all([
          getVendorPurchaseOrders({ size: 100, status: 'ACCEPTED' }),
          getVendorPurchaseOrders({ size: 100, status: 'IN_TRANSIT' }),
        ]);
        const accepted = getPageContent(acceptedRes?.data ?? acceptedRes);
        const inTransit = getPageContent(inTransitRes?.data ?? inTransitRes);
        const res = { data: { content: [...accepted, ...inTransit] } };
        const payload = res?.data ?? res;
        setOrders(getPageContent(payload));
      } catch {
        setOrders([]);
      } finally {
        setLoadingOrders(false);
      }
    };
    loadOrders();
  }, []);

  // Load PO detail when selection changes
  const loadPoDetail = useCallback(async (poId) => {
    if (!poId) { setPo(null); return; }
    setLoadingPo(true);
    setError('');
    try {
      const res = await getVendorPurchaseOrderDetail(poId);
      setPo(res?.data ?? res);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load order details.'));
      setPo(null);
    } finally {
      setLoadingPo(false);
    }
  }, []);

  useEffect(() => {
    if (selectedPoId) loadPoDetail(selectedPoId);
  }, [selectedPoId, loadPoDetail]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setFieldErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const errors = {};
    const needsDispatch = form.deliveryStatus !== 'PENDING';
    if (needsDispatch && !form.dispatchNumber.trim()) {
      errors.dispatchNumber = 'Dispatch number is required after shipment.';
    }
    if (form.dispatchDate && po?.createdAt) {
      const orderDate = new Date(po.createdAt).toISOString().split('T')[0];
      if (form.dispatchDate < orderDate) {
        errors.dispatchDate = 'Dispatch date cannot be before order date.';
      }
    }
    if (form.expectedDeliveryDate && form.dispatchDate) {
      if (form.expectedDeliveryDate < form.dispatchDate) {
        errors.expectedDeliveryDate = 'Expected delivery date cannot be before dispatch date.';
      }
    }
    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    const errors = validate();
    if (Object.keys(errors).length) { setFieldErrors(errors); return; }

    setSaving(true);
    try {
      await updateVendorDelivery({
        purchaseOrderId: Number(selectedPoId),
        deliveryStatus: form.deliveryStatus,
        dispatchNumber: form.dispatchNumber || null,
        dispatchDate: form.dispatchDate || null,
        expectedDeliveryDate: form.expectedDeliveryDate || null,
        remarks: form.remarks || null,
      });
      setSuccess('Delivery updated successfully.');
      // Reload PO to reflect new status
      loadPoDetail(selectedPoId);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to update delivery.'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Update Delivery</h1>
        <p className="text-muted mb-0">Update shipment and delivery information for a purchase order.</p>
      </div>

      {error ? <div className="alert alert-danger">{error}</div> : null}
      {success ? <div className="alert alert-success">{success}</div> : null}

      <div className="row g-4">
        {/* PO Selector */}
        <div className="col-12">
          <div className="employee-form-card">
            <label className="form-label fw-semibold" htmlFor="poSelect">
              Select Purchase Order
            </label>
            {loadingOrders ? (
              <div className="spinner-border spinner-border-sm text-primary" role="status" />
            ) : (
              <select
                id="poSelect"
                className="form-select"
                value={selectedPoId}
                onChange={(e) => { setSelectedPoId(e.target.value); setSuccess(''); setError(''); }}
              >
                <option value="">— Select an accepted order —</option>
                {orders.map((o) => (
                  <option key={o.id} value={o.id}>
                    {o.purchaseOrderNumber} — {o.departmentName}
                  </option>
                ))}
              </select>
            )}
          </div>
        </div>

        {loadingPo && (
          <div className="col-12 text-center py-3">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        )}

        {po && !loadingPo && (
          <>
            {/* Read-only PO Info */}
            <div className="col-lg-5">
              <div className="employee-detail-card h-100">
                <h2 className="h6 fw-bold mb-3">Order Details (Read Only)</h2>
                <dl className="employee-detail-list">
                  <div><dt>PO Number</dt><dd>{po.purchaseOrderNumber}</dd></div>
                  <div><dt>Department</dt><dd>{po.departmentName || '—'}</dd></div>
                  <div><dt>Order Date</dt><dd>{formatDate(po.createdAt)}</dd></div>
                  <div><dt>Delivery Date</dt><dd>{formatDate(po.expectedDeliveryDate)}</dd></div>
                  <div><dt>Total Amount</dt><dd className="fw-semibold">{formatCurrency(po.totalAmount)}</dd></div>
                  <div><dt>Status</dt><dd><VendorStatusBadge status={po.status} /></dd></div>
                </dl>

                {po.items?.length ? (
                  <>
                    <hr />
                    <h3 className="h6 fw-bold mb-2">Products</h3>
                    <div className="table-responsive">
                      <table className="table table-sm employee-table align-middle mb-0">
                        <thead>
                          <tr><th>Product</th><th>Qty</th><th>Total</th></tr>
                        </thead>
                        <tbody>
                          {po.items.map((item) => (
                            <tr key={item.id}>
                              <td>{item.productName}</td>
                              <td>{item.quantity}</td>
                              <td>{formatCurrency(item.totalPrice)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </>
                ) : null}
              </div>
            </div>

            {/* Editable Delivery Form */}
            <div className="col-lg-7">
              <div className="employee-form-card h-100">
                <h2 className="h6 fw-bold mb-3">Delivery Information</h2>
                <form onSubmit={handleSubmit} noValidate>
                  <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="deliveryStatus">
                      Delivery Status <span className="text-danger">*</span>
                    </label>
                    <select
                      id="deliveryStatus"
                      name="deliveryStatus"
                      className="form-select"
                      value={form.deliveryStatus}
                      onChange={handleChange}
                      required
                    >
                      {DELIVERY_STATUSES.map((s) => (
                        <option key={s.value} value={s.value}>{s.label}</option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="dispatchNumber">
                      Dispatch Number
                      {form.deliveryStatus !== 'PENDING' && <span className="text-danger"> *</span>}
                    </label>
                    <input
                      id="dispatchNumber"
                      name="dispatchNumber"
                      type="text"
                      className={`form-control ${fieldErrors.dispatchNumber ? 'is-invalid' : ''}`}
                      value={form.dispatchNumber}
                      onChange={handleChange}
                      placeholder="e.g. DISP-2024-001"
                    />
                    {fieldErrors.dispatchNumber && (
                      <div className="invalid-feedback">{fieldErrors.dispatchNumber}</div>
                    )}
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="dispatchDate">Dispatch Date</label>
                    <input
                      id="dispatchDate"
                      name="dispatchDate"
                      type="date"
                      className={`form-control ${fieldErrors.dispatchDate ? 'is-invalid' : ''}`}
                      value={form.dispatchDate}
                      onChange={handleChange}
                    />
                    {fieldErrors.dispatchDate && (
                      <div className="invalid-feedback">{fieldErrors.dispatchDate}</div>
                    )}
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" htmlFor="expectedDeliveryDate">
                      Expected Delivery Date
                    </label>
                    <input
                      id="expectedDeliveryDate"
                      name="expectedDeliveryDate"
                      type="date"
                      className={`form-control ${fieldErrors.expectedDeliveryDate ? 'is-invalid' : ''}`}
                      value={form.expectedDeliveryDate}
                      onChange={handleChange}
                    />
                    {fieldErrors.expectedDeliveryDate && (
                      <div className="invalid-feedback">{fieldErrors.expectedDeliveryDate}</div>
                    )}
                  </div>

                  <div className="mb-4">
                    <label className="form-label fw-semibold" htmlFor="remarks">Remarks</label>
                    <textarea
                      id="remarks"
                      name="remarks"
                      className="form-control"
                      rows={3}
                      value={form.remarks}
                      onChange={handleChange}
                      placeholder="Optional notes..."
                    />
                  </div>

                  <div className="d-flex gap-2">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={saving}
                    >
                      {saving ? <span className="spinner-border spinner-border-sm me-2" /> : null}
                      Update Delivery
                    </button>
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={() => navigate('/vendor/purchase-orders')}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
};

export default VendorUpdateDeliveryPage;
