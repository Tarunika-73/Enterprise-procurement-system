import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';
import { getProcurementDashboardStats } from '../../services/procurementService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getDisplayName } from '../../utils/userDisplay';
import { unwrapApiData } from '../../utils/employeeHelpers';

const ProcurementOfficerDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;
    getProcurementDashboardStats()
      .then((res) => { if (mounted) setStats(unwrapApiData(res)); })
      .catch((err) => { if (mounted) setError(getApiErrorMessage(err, 'Unable to load dashboard.')); })
      .finally(() => { if (mounted) setLoading(false); });
    return () => { mounted = false; };
  }, []);

  const statCards = [
    { icon: 'bi-check-circle',  iconVariant: 'success', value: loading ? '…' : String(stats?.approvedRequests   ?? 0), label: 'Approved Requests'  },
    { icon: 'bi-cart-check',    iconVariant: 'primary', value: loading ? '…' : String(stats?.totalPurchaseOrders ?? 0), label: 'Purchase Orders'    },
    { icon: 'bi-building',      iconVariant: 'warning', value: loading ? '…' : String(stats?.activeVendors       ?? 0), label: 'Active Vendors'     },
    { icon: 'bi-truck',         iconVariant: 'accent',  value: loading ? '…' : String(stats?.pendingDeliveries   ?? 0), label: 'Pending Deliveries' },
  ];

  return (
    <>
      <div className="dashboard-welcome-banner">
        <div>
          <h2>Welcome back, {getDisplayName(user)}</h2>
          <p className="text-muted mb-0">
            Manage approved purchase requests, create purchase orders, and assign vendors.
          </p>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>Procurement Officer Dashboard</h1>
        <p className="text-muted mb-0">Live summary of procurement activity.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row g-4">
        {statCards.map((card) => (
          <div key={card.label} className="col-sm-6 col-xl-3">
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>
    </>
  );
};

export default ProcurementOfficerDashboard;
