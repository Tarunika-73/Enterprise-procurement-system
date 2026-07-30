import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const MANAGER_STATS = [
  {
    icon: 'bi-hourglass-split',
    iconVariant: 'warning',
    value: '15',
    label: 'Pending Requests',
  },
  {
    icon: 'bi-check2-circle',
    iconVariant: 'success',
    value: '32',
    label: 'Approved Requests',
  },
  {
    icon: 'bi-x-circle',
    iconVariant: 'danger',
    value: '4',
    label: 'Rejected Requests',
  },
  {
    icon: 'bi-building',
    iconVariant: 'primary',
    value: '18',
    label: 'Department Requests',
  },
];

const ManagerDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Manager Dashboard"
      description="Review, approve, or reject department purchase requests."
      statCards={MANAGER_STATS}
    />
  );
};

export default ManagerDashboard;