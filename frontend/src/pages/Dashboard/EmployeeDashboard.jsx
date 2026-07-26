import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const EMPLOYEE_STATS = [
  { icon: 'bi-file-earmark-text', iconVariant: 'primary', value: '—', label: 'My Purchase Requests' },
  { icon: 'bi-hourglass-split', iconVariant: 'warning', value: '—', label: 'Pending Approvals' },
  { icon: 'bi-cart-check', iconVariant: 'accent', value: '—', label: 'Recent Orders' },
  { icon: 'bi-bell', iconVariant: 'success', value: '—', label: 'Notifications' },
];

const EmployeeDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Employee Dashboard"
      description="Manage your purchase requests, track approvals, and monitor order status."
      statCards={EMPLOYEE_STATS}
    />
  );
};

export default EmployeeDashboard;
