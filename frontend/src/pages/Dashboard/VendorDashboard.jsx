import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const VENDOR_STATS = [
  { icon: 'bi-box-seam', iconVariant: 'primary', value: '—', label: 'Active Orders' },
  { icon: 'bi-receipt', iconVariant: 'accent', value: '—', label: 'Pending Invoices' },
  { icon: 'bi-truck', iconVariant: 'warning', value: '—', label: 'Deliveries In Transit' },
  { icon: 'bi-star', iconVariant: 'success', value: '—', label: 'Performance Rating' },
];

const VendorDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Vendor Dashboard"
      description="View purchase orders, submit invoices, and track delivery schedules."
      statCards={VENDOR_STATS}
    />
  );
};

export default VendorDashboard;
