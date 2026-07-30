import DashboardPageContent from '../components/dashboard/DashboardPageContent';

const DEPARTMENT_REQUEST_STATS = [
  {
    icon: 'bi-building',
    iconVariant: 'primary',
    value: '18',
    label: 'Department Requests',
  },
  {
    icon: 'bi-hourglass-split',
    iconVariant: 'warning',
    value: '7',
    label: 'Pending',
  },
  {
    icon: 'bi-check-circle',
    iconVariant: 'success',
    value: '9',
    label: 'Approved',
  },
  {
    icon: 'bi-x-circle',
    iconVariant: 'danger',
    value: '2',
    label: 'Rejected',
  },
];

const DepartmentRequests = () => {
  return (
    <DashboardPageContent
      roleLabel="Department Requests"
      description="Monitor purchase requests submitted by your department."
      statCards={DEPARTMENT_REQUEST_STATS}
    />
  );
};

export default DepartmentRequests;