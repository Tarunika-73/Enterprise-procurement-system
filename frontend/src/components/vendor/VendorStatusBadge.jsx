const STATUS_MAP = {
  SENT: { label: 'Pending Delivery', variant: 'warning' },
  ACCEPTED: { label: 'Accepted', variant: 'info' },
  CREATED: { label: 'Created', variant: 'secondary' },
  REJECTED: { label: 'Rejected', variant: 'danger' },
  DELIVERED: { label: 'Delivered', variant: 'success' },
  CLOSED: { label: 'Closed', variant: 'secondary' },
  CANCELLED: { label: 'Cancelled', variant: 'danger' },
  // Delivery statuses
  PENDING: { label: 'Pending', variant: 'warning' },
  IN_TRANSIT: { label: 'In Transit', variant: 'info' },
  FAILED: { label: 'Failed', variant: 'danger' },
};

const VendorStatusBadge = ({ status }) => {
  const { label, variant } = STATUS_MAP[status] ?? { label: status ?? '—', variant: 'secondary' };
  return <span className={`badge text-bg-${variant} employee-status-badge`}>{label}</span>;
};

export default VendorStatusBadge;
