import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import RequestTimeline from '../../components/employee/RequestTimeline';
import StatusBadge from '../../components/employee/StatusBadge';
import { getPurchaseRequestById } from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import {
  formatCurrency,
  formatDate,
  formatDateTime,
  unwrapApiData,
} from '../../utils/employeeHelpers';

const RequestDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const backPath = pathname.startsWith('/manager/')
    ? '/dashboard/manager'
    : '/employee/purchase-requests';

  const backLabel = pathname.startsWith('/manager/')
    ? 'Back to Manager Dashboard'
    : 'Back to My Requests';

  const [request, setRequest] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    const loadDetails = async () => {
      setLoading(true);
      setError('');

      try {
        const response = await getPurchaseRequestById(id);

        if (mounted) {
          setRequest(unwrapApiData(response));
        }
      } catch (err) {
        if (mounted) {
          setError(getApiErrorMessage(err, 'Unable to load request details.'));
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    loadDetails();

    return () => {
      mounted = false;
    };
  }, [id]);

  if (loading) {
    return (
      <div className="employee-form-card text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger" role="alert">
        {error}

        <div className="mt-3">
          <button
            type="button"
            className="btn btn-outline-secondary btn-sm"
            onClick={() => navigate(-1)}
          >
            Go Back
          </button>
        </div>
      </div>
    );
  }

  if (!request) return null;

  // ---------------------------------------
  // Friendly display status
  // ---------------------------------------

  const hasDeliveredStage = (request.timeline || []).some(
    (entry) => entry.stage === 'Order Delivered'
  );

  const displayStatus = hasDeliveredStage
    ? 'Delivered'
    : request.status === 'CLOSED'
    ? 'Delivered'
    : request.status;

  return (
    <>
      <div className="dashboard-page-header d-flex flex-wrap justify-content-between gap-3">
        <div>
          <h1>Request Details</h1>

          <p className="text-muted mb-0">
            {request.requestNumber} · <StatusBadge status={displayStatus} />
          </p>
        </div>

        <Link
          to={backPath}
          className="btn btn-outline-secondary align-self-start"
        >
          {backLabel}
        </Link>
      </div>

      <div className="row g-4">
        {/* Employee Information */}

        <div className="col-lg-6">
          <section className="employee-detail-card">
            <h2 className="h6">Employee Information</h2>

            <dl className="employee-detail-list">
              <div>
                <dt>Employee</dt>
                <dd>{request.requesterName || '—'}</dd>
              </div>

              <div>
                <dt>Employee Code</dt>
                <dd>{request.employeeCode || '—'}</dd>
              </div>

              <div>
                <dt>Department</dt>
                <dd>{request.departmentName || '—'}</dd>
              </div>

              <div>
                <dt>Assigned Manager</dt>
                <dd>{request.managerName || request.currentApproverName || '—'}</dd>
              </div>
            </dl>
          </section>
        </div>

        {/* Product Information */}

        <div className="col-lg-6">
          <section className="employee-detail-card">
            <h2 className="h6">Product Information</h2>

            <dl className="employee-detail-list">
              <div>
                <dt>Product</dt>
                <dd>{request.productName || '—'}</dd>
              </div>

              <div>
                <dt>SKU</dt>
                <dd>{request.productSku || '—'}</dd>
              </div>

              <div>
                <dt>Category</dt>
                <dd>{request.categoryName || '—'}</dd>
              </div>
            </dl>
          </section>
        </div>

        {/* Purchase Details */}

        <div className="col-lg-6">
          <section className="employee-detail-card">
            <h2 className="h6">Purchase Details</h2>

            <dl className="employee-detail-list">
              <div>
                <dt>Title</dt>
                <dd>{request.title || '—'}</dd>
              </div>

              <div>
                <dt>Quantity</dt>
                <dd>{request.quantity ?? '—'}</dd>
              </div>

              <div>
                <dt>Unit Price</dt>
                <dd>{formatCurrency(request.unitPrice)}</dd>
              </div>

              <div>
                <dt>Total Amount</dt>
                <dd>{formatCurrency(request.totalAmount)}</dd>
              </div>

              <div>
                <dt>Priority</dt>
                <dd>
                  <StatusBadge status={request.priority} />
                </dd>
              </div>

              <div>
                <dt>Expected Delivery</dt>
                <dd>{formatDate(request.expectedDeliveryDate)}</dd>
              </div>

              <div>
                <dt>Requested On</dt>
                <dd>{formatDateTime(request.createdAt)}</dd>
              </div>

              <div>
                <dt>Justification</dt>
                <dd>{request.justification || '—'}</dd>
              </div>
            </dl>
          </section>
        </div>

        {/* Current Status */}

        <div className="col-lg-6">
          <section className="employee-detail-card">
            <h2 className="h6">Current Status & Remarks</h2>

            <dl className="employee-detail-list">
              <div>
                <dt>Current Status</dt>

                <dd>
                  <StatusBadge status={displayStatus} />
                </dd>
              </div>

              <div>
                <dt>Approval Date</dt>
                <dd>{formatDateTime(request.approvalDate)}</dd>
              </div>

              <div>
                <dt>Current Approver</dt>
                <dd>{request.currentApproverName || '—'}</dd>
              </div>

              <div>
                <dt>Manager Remarks</dt>
                <dd>{request.managerRemarks || 'No remarks yet.'}</dd>
              </div>
            </dl>
          </section>
        </div>

        {/* Timeline */}

        <div className="col-12">
          <section className="employee-detail-card">
            <h2 className="h6 mb-3">Approval Timeline</h2>

            <RequestTimeline timeline={request.timeline || []} />
          </section>
        </div>

        {/* History */}

        <div className="col-12">
          <section className="employee-detail-card">
            <h2 className="h6 mb-3">Request History</h2>

            <div className="table-responsive">
              <table className="table employee-table align-middle mb-0">
                <thead>
                  <tr>
                    <th>Event</th>
                    <th>Actor</th>
                    <th>Status</th>
                    <th>Timestamp</th>
                  </tr>
                </thead>

                <tbody>
                  {(request.timeline || []).map((entry, index) => (
                    <tr key={`${entry.stage}-${index}`}>
                      <td>{entry.stage}</td>

                      <td>{entry.actorName || '—'}</td>

                      <td>
                        <StatusBadge status={entry.status} />
                      </td>

                      <td>{formatDateTime(entry.timestamp)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>
    </>
  );
};

export default RequestDetailsPage;