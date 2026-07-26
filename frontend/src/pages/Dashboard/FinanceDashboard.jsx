import DashboardPageContent from '../../components/dashboard/DashboardPageContent';

const FINANCE_STATS = [
  { icon: 'bi-cash-stack', iconVariant: 'primary', value: '—', label: 'Pending Payments' },
  { icon: 'bi-wallet2', iconVariant: 'accent', value: '—', label: 'Budget Allocation' },
  { icon: 'bi-receipt-cutoff', iconVariant: 'warning', value: '—', label: 'Invoices to Review' },
  { icon: 'bi-bar-chart-line', iconVariant: 'success', value: '—', label: 'Financial Reports' },
];

const FinanceDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Finance Dashboard"
      description="Process payments, manage budgets, and review financial procurement reports."
      statCards={FINANCE_STATS}
    />
  );
};

export default FinanceDashboard;
