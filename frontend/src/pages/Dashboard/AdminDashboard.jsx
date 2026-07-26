import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const ADMIN_STATS = [
  { icon: 'bi-person-gear', iconVariant: 'primary', value: '—', label: 'User Management' },
  { icon: 'bi-shield-check', iconVariant: 'accent', value: '—', label: 'Role Permissions' },
  { icon: 'bi-journal-text', iconVariant: 'warning', value: '—', label: 'Audit Logs' },
  { icon: 'bi-sliders', iconVariant: 'success', value: '—', label: 'System Settings' },
];

const AdminDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Admin Dashboard"
      description="Configure system settings, manage users, and oversee platform operations."
      statCards={ADMIN_STATS}
    />
  );
};

export default AdminDashboard;
