import DashboardPageContent from '../components/dashboard/DashboardPageContent';

const SUPPLIER_STATS = [
  {
    icon: 'bi-people',
    iconVariant: 'primary',
    value: '20',
    label: 'Suppliers',
  },
  {
    icon: 'bi-patch-check',
    iconVariant: 'success',
    value: '18',
    label: 'Active',
  },
  {
    icon: 'bi-clock-history',
    iconVariant: 'warning',
    value: '2',
    label: 'Pending Verification',
  },
  {
    icon: 'bi-award',
    iconVariant: 'info',
    value: '15',
    label: 'Preferred',
  },
];

const Suppliers = () => {
  return (
    <DashboardPageContent
      roleLabel="Vendor Management"
      description="Manage registered vendors and supplier information."
      statCards={SUPPLIER_STATS}
    />
  );
};

export default Suppliers;