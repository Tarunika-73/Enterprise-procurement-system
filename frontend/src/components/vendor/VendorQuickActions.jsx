import { Link } from 'react-router-dom';

const ACTIONS = [
  {
    to: '/vendor/purchase-orders',
    icon: 'bi-cart-check',
    label: 'View Purchase Orders',
    description: 'Browse all received orders',
  },
  {
    to: '/vendor/deliveries',
    icon: 'bi-truck',
    label: 'Update Delivery',
    description: 'Update shipment status',
  },
  {
    to: '/vendor/profile',
    icon: 'bi-person-circle',
    label: 'My Profile',
    description: 'View and edit company info',
  },
  {
    to: '/notifications',
    icon: 'bi-bell',
    label: 'Notifications',
    description: 'Check updates and alerts',
  },
];

const VendorQuickActions = () => (
  <div className="row g-3">
    {ACTIONS.map((action) => (
      <div key={action.to} className="col-sm-6 col-xl-3">
        <Link to={action.to} className="employee-quick-action">
          <div className="employee-quick-action-icon">
            <i className={`bi ${action.icon}`} aria-hidden="true" />
          </div>
          <div>
            <div className="employee-quick-action-label">{action.label}</div>
            <div className="employee-quick-action-desc">{action.description}</div>
          </div>
        </Link>
      </div>
    ))}
  </div>
);

export default VendorQuickActions;
