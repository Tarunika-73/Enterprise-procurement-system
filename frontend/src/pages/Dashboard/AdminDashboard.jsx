import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardStatCard from '../../components/dashboard/DashboardStatCard';
import DashboardAnalytics from '../../components/dashboard/DashboardAnalytics';
import { getAdminDashboardStats } from '../../services/adminService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getDisplayName } from '../../utils/userDisplay';
import { unwrapApiData } from '../../utils/employeeHelpers';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;
    getAdminDashboardStats()
      .then((res) => { if (mounted) setStats(unwrapApiData(res)); })
      .catch((err) => { if (mounted) setError(getApiErrorMessage(err, 'Unable to load dashboard.')); })
      .finally(() => { if (mounted) setLoading(false); });
    return () => { mounted = false; };
  }, []);

  const v = (key) => loading ? '…' : String(stats?.[key] ?? 0);

  const statCards = [
    { icon: 'bi-people',          iconVariant: 'primary', value: v('totalUsers'),            label: 'Total Users'           },
    { icon: 'bi-person-check',    iconVariant: 'success', value: v('activeUsers'),           label: 'Active Users'          },
    { icon: 'bi-person-x',        iconVariant: 'warning', value: v('inactiveUsers'),         label: 'Inactive Users'        },
    { icon: 'bi-building',        iconVariant: 'accent',  value: v('totalVendors'),          label: 'Total Vendors'         },
    { icon: 'bi-building-check',  iconVariant: 'success', value: v('activeVendors'),         label: 'Active Vendors'        },
    { icon: 'bi-clipboard-data',  iconVariant: 'primary', value: v('totalPurchaseRequests'), label: 'Purchase Requests'     },
    { icon: 'bi-hourglass-split', iconVariant: 'warning', value: v('pendingPurchaseRequests'), label: 'Pending Requests'    },
    { icon: 'bi-check-circle',    iconVariant: 'success', value: v('approvedPurchaseRequests'), label: 'Approved Requests'  },
    { icon: 'bi-cart-check',      iconVariant: 'primary', value: v('totalPurchaseOrders'),   label: 'Purchase Orders'       },
    { icon: 'bi-receipt',         iconVariant: 'accent',  value: v('totalInvoices'),         label: 'Total Invoices'        },
    { icon: 'bi-clock-history',   iconVariant: 'warning', value: v('pendingPayments'),       label: 'Pending Payments'      },
    { icon: 'bi-currency-rupee',  iconVariant: 'success', value: v('completedPayments'),     label: 'Completed Payments'    },
  ];

  return (
    <>
      <div className="dashboard-welcome-banner">
        <div>
          <h2>Welcome back, {getDisplayName(user)}</h2>
          <p className="text-muted mb-0">System-wide overview of the Enterprise Procurement System.</p>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>Admin Dashboard</h1>
        <p className="text-muted mb-0">Live system statistics.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row g-4">
        {statCards.map((card) => (
          <div key={card.label} className="col-sm-6 col-xl-3">
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>
      <DashboardAnalytics />
    </>
  );
};

export default AdminDashboard;
