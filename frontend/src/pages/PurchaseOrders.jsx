import DashboardPageContent from '../components/dashboard/DashboardPageContent';

const PURCHASE_ORDER_STATS = [
  {
    icon: 'bi-cart-check',
    iconVariant: 'primary',
    value: '12',
    label: 'Purchase Orders',
  },
  {
    icon: 'bi-truck',
    iconVariant: 'warning',
    value: '5',
    label: 'In Delivery',
  },
  {
    icon: 'bi-check-circle',
    iconVariant: 'success',
    value: '18',
    label: 'Completed',
  },
  {
    icon: 'bi-clock-history',
    iconVariant: 'info',
    value: '3',
    label: 'Pending',
  },
];

const PurchaseOrders = () => {
  return (
    <DashboardPageContent
      roleLabel="Purchase Orders"
      description="Manage purchase orders issued to vendors."
      statCards={PURCHASE_ORDER_STATS}
    />
  );
};

export default PurchaseOrders;