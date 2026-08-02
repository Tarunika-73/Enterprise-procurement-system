import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';
import QuickActions from '../../components/employee/QuickActions';
import RequestTable from '../../components/employee/RequestTable';
import { getEmployeeDashboardStats } from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getDisplayName } from '../../utils/userDisplay';
import { unwrapApiData } from '../../utils/employeeHelpers';

const EmployeeDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    const loadStats = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await getEmployeeDashboardStats();
        const data = unwrapApiData(response);
        if (mounted) setStats(data);
      } catch (err) {
        if (mounted) {
          setError(getApiErrorMessage(err, 'Unable to load dashboard data.'));
        }
      } finally {
        if (mounted) setLoading(false);
      }
    };

    loadStats();
    return () => {
      mounted = false;
    };
  }, []);

  const statCards = [
    {
      icon: 'bi-file-earmark-text',
      iconVariant: 'primary',
      value: loading ? '…' : String(stats?.totalRequests ?? 0),
      label: 'Total Purchase Requests',
    },
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
  ];

  return (
    <>
      <div className="dashboard-welcome-banner">
        <div className="d-flex flex-wrap align-items-start justify-content-between gap-2">
          <div>
            <h2>Welcome back, {getDisplayName(user)}</h2>
            <p className="text-muted mb-0">
              {user?.departmentName ? (
                <><i className="bi bi-building me-1" aria-hidden="true" />{user.departmentName} · </>
              ) : null}
              Manage your purchase requests, track approvals, and monitor order status.
            </p>
          </div>
          <Link to="/employee/purchase-requests/create" className="btn btn-primary">
            Create Request
          </Link>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>Employee Dashboard Overview</h1>
        <p className="text-muted mb-0">
          Live summary of your procurement activity.
        </p>
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

      <div className="mb-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h2 className="h5 mb-0">Quick Actions</h2>
        </div>
        <QuickActions />
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2 className="h5 mb-0">Recent Purchase Requests</h2>
        <Link to="/employee/purchase-requests" className="btn btn-sm btn-outline-primary">
          View All
        </Link>
      </div>

      <RequestTable
        requests={stats?.recentRequests || []}
        loading={loading}
        onViewDetails={(id) => navigate(`/employee/purchase-requests/${id}`)}
        emptyMessage="You have not submitted any purchase requests yet."
      />
    </>
  );
};

export default EmployeeDashboard;
