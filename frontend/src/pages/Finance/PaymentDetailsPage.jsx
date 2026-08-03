import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { approvePayment, cancelPayment, getPendingPayments } from '../../services/financeService';
import { formatCurrency, formatDate } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const PAYMENT_METHODS = ['Bank Transfer', 'Cheque', 'NEFT', 'RTGS', 'IMPS', 'UPI', 'Cash'];

const PaymentDetailsPage = () => {
  const { purchaseOrderId } = useParams();
  const navigate = useNavigate();

  const [po, setPo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  const [paymentMethod, setPaymentMethod] = useState('Bank Transfer');
  const [remarks, setRemarks] = useState('');
  const [formError, setFormError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      // Fetch from pending-payments and find the matching PO
      const res = await getPendingPayments({ page: 0, size: 100 });
      const list = res?.data?.content ?? res?.content ?? [];
      const found = list.find((p) => String(p.purchaseOrderId) === String(purchaseOrderId));
      if (!found) throw new Error('Purchase order not found in pending payments.');
      setPo(found);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load payment details.'));
    } finally {
      setLoading(false);
    }
  }, [purchaseOrderId]);

  useEffect(() => { load(); }, [load]);

  const handleApprove = async (e) => {
    e.preventDefault();
    setFormError('');
    if (!paymentMethod) {
      setFormError('Payment method is required.');
      return;
    }
    setSubmitting(true);
    try {
      await approvePayment(purchaseOrderId, { paymentMethod, remarks });
      setSuccessMsg('Payment approved successfully! Purchase Order is now CLOSED.');
      setTimeout(() => navigate('/finance/payment-history'), 2000);
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'Failed to approve payment.'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Are you sure you want to cancel this payment?')) return;
    setSubmitting(true);
    try {
      await cancelPayment(purchaseOrderId);
      setSuccessMsg('Payment cancelled.');
      setTimeout(() => navigate('/finance/pending-payments'), 1500);
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'Failed to cancel payment.'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status" />
      </div>
    );
  }

  if (error) {
    return (
      <>
        <div className="alert alert-danger">{error}</div>
        <button type="button" className="btn btn-outline-secondary" onClick={() => navigate(-1)}>
          Back
        </button>
      </>
    );
  }

  return (
    <>
      <div className="dashboard-page-header d-flex justify-content-between align-items-start flex-wrap gap-2">
        <div>
          <h1>Payment Details</h1>
          <p className="text-muted mb-0">Review and approve payment for {po?.purchaseOrderNumber}.</p>
        </div>
        <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => navigate(-1)}>
          <i className="bi bi-arrow-left me-1" /> Back
        </button>
      </div>

      {successMsg && <div className="alert alert-success">{successMsg}</div>}
      {formError && <div className="alert alert-danger">{formError}</div>}

      <div className="row g-3">
        {/* PO Info */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="card-title fw-semibold mb-3">Purchase Order Information</h6>
              <dl className="row mb-0">
                <dt className="col-5 text-muted small">PO Number</dt>
                <dd className="col-7 fw-semibold">{po?.purchaseOrderNumber}</dd>

                <dt className="col-5 text-muted small">Request Number</dt>
                <dd className="col-7">{po?.requestNumber ?? '—'}</dd>

                <dt className="col-5 text-muted small">Vendor</dt>
                <dd className="col-7">{po?.vendorName ?? '—'}</dd>

                <dt className="col-5 text-muted small">Vendor Email</dt>
                <dd className="col-7">{po?.vendorEmail ?? '—'}</dd>

                <dt className="col-5 text-muted small">Department</dt>
                <dd className="col-7">{po?.departmentName ?? '—'}</dd>

                <dt className="col-5 text-muted small">Amount</dt>
                <dd className="col-7 fw-bold text-success">{formatCurrency(po?.totalAmount)}</dd>

                <dt className="col-5 text-muted small">Expected Delivery</dt>
                <dd className="col-7">{formatDate(po?.expectedDeliveryDate)}</dd>

                <dt className="col-5 text-muted small">Actual Delivery</dt>
                <dd className="col-7">{formatDate(po?.deliveryDate)}</dd>

                <dt className="col-5 text-muted small">Invoice #</dt>
                <dd className="col-7">{po?.invoiceNumber ?? <span className="text-muted">—</span>}</dd>

                <dt className="col-5 text-muted small">Status</dt>
                <dd className="col-7">
                  <span className="badge bg-warning text-dark">DELIVERED</span>
                </dd>
              </dl>
            </div>
          </div>
        </div>

        {/* Approve Form */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="card-title fw-semibold mb-3">Approve Payment</h6>
              <form onSubmit={handleApprove}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold" htmlFor="paymentMethod">
                    Payment Method <span className="text-danger">*</span>
                  </label>
                  <select
                    id="paymentMethod"
                    className="form-select"
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    required
                  >
                    {PAYMENT_METHODS.map((m) => (
                      <option key={m} value={m}>{m}</option>
                    ))}
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold" htmlFor="remarks">
                    Remarks
                  </label>
                  <textarea
                    id="remarks"
                    className="form-control"
                    rows={3}
                    maxLength={500}
                    value={remarks}
                    onChange={(e) => setRemarks(e.target.value)}
                    placeholder="Optional remarks..."
                  />
                </div>

                <div className="d-flex gap-2">
                  <button
                    type="submit"
                    className="btn btn-success flex-grow-1"
                    disabled={submitting}
                  >
                    {submitting ? (
                      <span className="spinner-border spinner-border-sm me-2" />
                    ) : (
                      <i className="bi bi-check-circle me-2" />
                    )}
                    Approve Payment
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-danger"
                    disabled={submitting}
                    onClick={handleCancel}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default PaymentDetailsPage;
