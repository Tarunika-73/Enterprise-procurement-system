import { Link } from 'react-router-dom';

const QuickActions = () => {
  const actions = [
    {
      to: '/employee/purchase-requests/create',
      icon: 'bi-plus-circle',
      label: 'Create Purchase Request',
      description: 'Raise a new procurement request',
    },
    {
      to: '/employee/products',
      icon: 'bi-box-seam',
      label: 'View Products',
      description: 'Browse the product catalog',
    },
    {
      to: '/employee/purchase-requests',
      icon: 'bi-list-check',
      label: 'Track My Requests',
      description: 'Monitor approval status',
    },
    {
      to: '/notifications',
      icon: 'bi-bell',
      label: 'View Notifications',
      description: 'Check updates and alerts',
    },
  ];

  return (
    <div className="row g-3">
      {actions.map((action) => (
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
};

export default QuickActions;
