import StatusBadge from './StatusBadge';
import { formatCurrency, formatDate } from '../../utils/employeeHelpers';

const RequestTable = ({
  requests = [],
  loading = false,
  onViewDetails,
  emptyMessage = 'No purchase requests found.',
}) => {
  if (loading) {
    return (
      <div className="employee-table-card text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!requests.length) {
    return (
      <div className="employee-table-card text-center py-5 text-muted">
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="employee-table-card table-responsive">
      <table className="table employee-table align-middle mb-0">
        <thead>
          <tr>
            <th>Request ID</th>
            <th>Title</th>
            <th>Product</th>
            <th>Qty</th>
            <th>Amount</th>
            <th>Requested Date</th>
            <th>Priority</th>
            <th>Status</th>
            <th>Current Approver</th>
            <th>Remarks</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {requests.map((request) => (
            <tr key={request.id}>
              <td>{request.requestNumber}</td>
              <td>{request.title || '—'}</td>
              <td>{request.productName || '—'}</td>
              <td>{request.quantity ?? '—'}</td>
              <td>{formatCurrency(request.totalAmount)}</td>
              <td>{formatDate(request.createdAt)}</td>
              <td>
                <StatusBadge status={request.priority} />
              </td>
              <td>
                <StatusBadge status={request.status} />
              </td>
              <td>{request.currentApproverName || '—'}</td>
              <td className="text-truncate" style={{ maxWidth: 160 }}>
                {request.managerRemarks || '—'}
              </td>
              <td>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-primary"
                  onClick={() => onViewDetails?.(request.id)}
                >
                  View Details
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default RequestTable;
