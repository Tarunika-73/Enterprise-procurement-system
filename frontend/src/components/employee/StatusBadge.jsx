import { formatStatusLabel, getStatusBadgeClass } from '../../utils/employeeHelpers';

const StatusBadge = ({ status }) => {
  const variant = getStatusBadgeClass(status);
  return (
    <span className={`badge text-bg-${variant} employee-status-badge`}>
      {formatStatusLabel(status)}
    </span>
  );
};

export default StatusBadge;
