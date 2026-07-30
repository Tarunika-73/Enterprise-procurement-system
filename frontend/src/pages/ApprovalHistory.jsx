import DashboardPageContent from '../components/dashboard/DashboardPageContent';

const APPROVAL_HISTORY_STATS = [
  {
    icon: 'bi-check-circle',
    iconVariant: 'success',
    value: '32',
    label: 'Approved',
  },
  {
    icon: 'bi-x-circle',
    iconVariant: 'danger',
    value: '4',
    label: 'Rejected',
  },
  {
    icon: 'bi-clock-history',
    iconVariant: 'warning',
    value: '12',
    label: 'Pending',
  },
  {
    icon: 'bi-calendar-event',
    iconVariant: 'primary',
    value: '48',
    label: 'Total Requests',
  },
];

const ApprovalHistory = () => {
  return (
    <DashboardPageContent
      roleLabel="Approval History"
      description="View all purchase request approval decisions."
      statCards={APPROVAL_HISTORY_STATS}
    />
  );
};

export default ApprovalHistory;