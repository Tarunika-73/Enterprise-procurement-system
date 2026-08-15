import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';
import DashboardAnalytics from '../../components/dashboard/DashboardAnalytics';
import VendorQuickActions from '../../components/vendor/VendorQuickActions';
import VendorPOTable from '../../components/vendor/VendorPOTable';
import { getVendorDashboard } from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getDisplayName } from '../../utils/userDisplay';
import { formatCurrency } from '../../utils/employeeHelpers';

const VendorDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await getVendorDashboard();
        if (mounted) setData(res?.data ?? res);
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Unable to load dashboard data.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, []);

  const statCards = [
    {
      icon: 'bi-cart-check',
      iconVariant: 'primary',
      value: loading ? '…' : String(data?.totalOrders ?? 0),
      label: 'Total Orders',
    },
    {
      icon: 'bi-hourglass-split',
      iconVariant: 'warning',
      value: loading ? '…' : String(data?.pendingDelivery ?? 0),
      label: 'Pending Delivery',
    },
    {
      icon: 'bi-check-circle',
      iconVariant: 'success',
      value: loading ? '…' : String(data?.deliveredOrders ?? 0),
      label: 'Delivered Orders',
    },
    {
      icon: 'bi-currency-rupee',
      iconVariant: 'accent',
      value: loading ? '…' : formatCurrency(data?.totalOrderValue ?? 0),
      label: 'Total Order Value',
    },
  ];

  return (
    <>
      <div className="dashboard-welcome-banner">
        <div className="d-flex flex-wrap align-items-start justify-content-between gap-2">
          <div>
            <h2>Welcome, {getDisplayName(user)}</h2>
            <p className="text-muted mb-0">
              {data?.vendorName ? (
                <><i className="bi bi-building me-1" aria-hidden="true" />{data.vendorName} · </>
              ) : null}
              Manage your purchase orders and update delivery information.
            </p>
          </div>
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => navigate('/vendor/purchase-orders')}
          >
            View Orders
          </button>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>Vendor Dashboard Overview</h1>
        <p className="text-muted mb-0">Live summary of your procurement activity.</p>
      </div>

      {error ? <div className="alert alert-danger" role="alert">{error}</div> : null}

      <div className="row g-4 mb-4">
        {statCards.map((card) => (
          <div key={card.label} className="col-sm-6 col-xl-3">
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>

      <DashboardAnalytics />

      <div className="mb-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h2 className="h5 mb-0">Quick Actions</h2>
        </div>
        <VendorQuickActions />
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2 className="h5 mb-0">Recent Purchase Orders</h2>
        <button
          type="button"
          className="btn btn-sm btn-outline-primary"
          onClick={() => navigate('/vendor/purchase-orders')}
        >
          View All
        </button>
      </div>

      <VendorPOTable
        orders={data?.recentOrders || []}
        loading={loading}
        onView={(id) => navigate(`/vendor/purchase-orders/${id}`)}
        emptyMessage="No purchase orders received yet."
      />
    </>
  );
};

export default VendorDashboard;
