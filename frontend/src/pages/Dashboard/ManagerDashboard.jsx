import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const MANAGER_STATS = [
  { icon: 'bi-people', iconVariant: 'primary', value: '—', label: 'Team Requests' },
  { icon: 'bi-check2-circle', iconVariant: 'warning', value: '—', label: 'Approvals Queue' },
  { icon: 'bi-pie-chart', iconVariant: 'accent', value: '—', label: 'Budget Overview' },
  { icon: 'bi-graph-up', iconVariant: 'success', value: '—', label: 'Department Reports' },
];

const ManagerDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Manager Dashboard"
      description="Review team purchase requests, approve budgets, and monitor procurement activity."
      statCards={MANAGER_STATS}
    />
  );
};

export default ManagerDashboard;
