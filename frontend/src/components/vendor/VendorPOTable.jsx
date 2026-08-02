import VendorStatusBadge from './VendorStatusBadge';
import { formatCurrency, formatDate } from '../../utils/employeeHelpers';

const VendorPOTable = ({ orders = [], loading = false, onView, emptyMessage = 'No purchase orders found.' }) => {
  if (loading) {
    return (
      <div className="employee-table-card text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!orders.length) {
    return <div className="employee-table-card text-center py-5 text-muted">{emptyMessage}</div>;
  }

  return (
    <div className="employee-table-card table-responsive">
      <table className="table employee-table align-middle mb-0">
        <thead>
          <tr>
            <th>PO Number</th>
            <th>Department</th>
            <th>Order Date</th>
            <th>Delivery Date</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((po) => (
            <tr key={po.id}>
              <td className="fw-semibold">{po.purchaseOrderNumber}</td>
              <td>{po.departmentName || '—'}</td>
              <td>{formatDate(po.createdAt)}</td>
              <td>{formatDate(po.expectedDeliveryDate)}</td>
              <td>{formatCurrency(po.totalAmount)}</td>
              <td><VendorStatusBadge status={po.status} /></td>
              <td>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-primary"
                  onClick={() => onView?.(po.id)}
                >
                  View
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default VendorPOTable;
