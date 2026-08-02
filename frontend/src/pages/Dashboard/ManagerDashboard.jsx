import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';
import StatusBadge from '../../components/employee/StatusBadge';
import Toast from '../../components/Authentication/Toast/Toast';
import { useAuth } from '../../context/AuthContext';
import {
  approvePurchaseRequest,
  getManagerDashboardStats,
  getManagerInbox,
  rejectPurchaseRequest,
  returnPurchaseRequest,
} from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import {
  formatCurrency,
  formatDate,
  getPageContent,
  unwrapApiData,
} from '../../utils/employeeHelpers';
import { getDisplayName } from '../../utils/userDisplay';

const ManagerDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState(null);
  const [remarksDraft, setRemarksDraft] = useState({});
  const [error, setError] = useState('');
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [statsResponse, inboxResponse] = await Promise.all([
        getManagerDashboardStats(),
        getManagerInbox({ page: 0, size: 20 }),
      ]);
      setStats(unwrapApiData(statsResponse));
      setRequests(getPageContent(inboxResponse));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load manager dashboard.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const getRemarks = (id) => (remarksDraft[id] || '').trim();

  const runAction = async (id, action) => {
    const remarks = getRemarks(id);
    if ((action === 'reject' || action === 'return') && !remarks) {
      setToast({
        show: true,
        message: 'Remarks are required for reject / return actions.',
        type: 'warning',
      });
      return;
    }

    setActionLoadingId(id);
    try {
      if (action === 'approve') {
        await approvePurchaseRequest(id, remarks);
      } else if (action === 'reject') {
        await rejectPurchaseRequest(id, remarks);
      } else {
        await returnPurchaseRequest(id, remarks);
      }
      setToast({
        show: true,
        message: 'Request updated successfully.',
        type: 'success',
      });
      await loadData();
    } catch (err) {
      setToast({
        show: true,
        message: getApiErrorMessage(err, 'Failed to update request.'),
        type: 'danger',
      });
    } finally {
      setActionLoadingId(null);
    }
  };

  const statCards = [
    {
      icon: 'bi-hourglass-split',
      iconVariant: 'warning',
      value: loading ? '…' : String(stats?.pendingRequests ?? 0),
      label: 'Pending Requests',
    },
    {
      icon: 'bi-check-circle',
      iconVariant: 'success',
      value: loading ? '…' : String(stats?.approvedRequests ?? 0),
      label: 'Approved Requests',
    },
    {
      icon: 'bi-x-circle',
      iconVariant: 'accent',
      value: loading ? '…' : String(stats?.rejectedRequests ?? 0),
      label: 'Rejected Requests',
    },
    {
      icon: 'bi-arrow-counterclockwise',
      iconVariant: 'primary',
      value: loading ? '…' : String(stats?.returnedRequests ?? 0),
      label: 'Returned Requests',
    },
  ];

  return (
    <>
      <Toast
        show={toast.show}
        message={toast.message}
        type={toast.type}
        onClose={() => setToast((prev) => ({ ...prev, show: false }))}
      />

      <div className="dashboard-welcome-banner">
        <div>
          <h2>Welcome back, {getDisplayName(user)}</h2>
          <p className="text-muted mb-0">
            Review purchase requests assigned to {user?.departmentName || 'your department'} only.
          </p>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>Manager Dashboard Overview</h1>
        <p className="text-muted mb-0">Approve, reject, or return requests with remarks.</p>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <div className="row g-4 mb-4">
        {statCards.map((card) => (
          <div key={card.label} className="col-sm-6 col-xl-3">
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>

      <h2 className="h5 mb-3">Assigned Purchase Requests</h2>

      <div className="employee-table-card table-responsive">
        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : requests.length === 0 ? (
          <div className="text-center text-muted py-5">No requests assigned to you yet.</div>
        ) : (
          <table className="table employee-table align-middle mb-0">
            <thead>
              <tr>
                <th>Request ID</th>
                <th>Employee</th>
                <th>Title</th>
                <th>Amount</th>
                <th>Date</th>
                <th>Status</th>
                <th>Remarks</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((request) => {
                const pending = request.status === 'PENDING';
                const busy = actionLoadingId === request.id;
                return (
                  <tr key={request.id}>
                    <td>{request.requestNumber}</td>
                    <td>{request.requesterName || '—'}</td>
                    <td>{request.title || '—'}</td>
                    <td>{formatCurrency(request.totalAmount)}</td>
                    <td>{formatDate(request.createdAt)}</td>
                    <td>
                      <StatusBadge status={request.status} />
                    </td>
                    <td style={{ minWidth: 180 }}>
                      {pending ? (
                        <input
                          className="form-control form-control-sm"
                          placeholder="Add remarks"
                          value={remarksDraft[request.id] || ''}
                          onChange={(event) =>
                            setRemarksDraft((prev) => ({
                              ...prev,
                              [request.id]: event.target.value,
                            }))
                          }
                        />
                      ) : (
                        <span className="text-muted small">{request.managerRemarks || '—'}</span>
                      )}
                    </td>
                    <td>
                      <div className="d-flex flex-wrap gap-1">
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => navigate(`/manager/requests/${request.id}`)}
                        >
                          View
                        </button>
                        {pending ? (
                          <>
                            <button
                              type="button"
                              className="btn btn-sm btn-success"
                              disabled={busy}
                              onClick={() => runAction(request.id, 'approve')}
                            >
                              Approve
                            </button>
                            <button
                              type="button"
                              className="btn btn-sm btn-danger"
                              disabled={busy}
                              onClick={() => runAction(request.id, 'reject')}
                            >
                              Reject
                            </button>
                            <button
                              type="button"
                              className="btn btn-sm btn-warning"
                              disabled={busy}
                              onClick={() => runAction(request.id, 'return')}
                            >
                              Return
                            </button>
                          </>
                        ) : null}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
};

export default ManagerDashboard;
