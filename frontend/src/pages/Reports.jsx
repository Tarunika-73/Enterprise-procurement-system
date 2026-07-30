import DashboardPageContent from '../components/dashboard/DashboardPageContent';

const REPORT_STATS = [
  {
    icon: 'bi-bar-chart',
    iconVariant: 'primary',
    value: '10',
    label: 'Reports',
  },
  {
    icon: 'bi-graph-up-arrow',
    iconVariant: 'success',
    value: '98%',
    label: 'Efficiency',
  },
  {
    icon: 'bi-currency-rupee',
    iconVariant: 'warning',
    value: '₹2.5M',
    label: 'Procurement Value',
  },
  {
    icon: 'bi-calendar-check',
    iconVariant: 'info',
    value: '30',
    label: 'Monthly Summary',
  },
];

const Reports = () => {
  return (
    <DashboardPageContent
      roleLabel="Reports"
      description="View procurement reports and analytics."
      statCards={REPORT_STATS}
    />
  );
};

export default Reports;