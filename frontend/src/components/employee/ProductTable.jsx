import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import { formatCurrency } from '../../utils/employeeHelpers';

const ProductTable = ({
  products = [],
  loading = false,
  emptyMessage = 'No products found.',
  showDepartment = true,
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

  if (!products.length) {
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
            <th>Product ID</th>
            <th>Product Name</th>
            <th>Category</th>
            {showDepartment ? <th>Department</th> : null}
            <th>Price</th>
            <th>Available Qty</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => {
            const canRequest =
              product.status === 'ACTIVE' && Number(product.availableQuantity) > 0;

            return (
              <tr key={product.id}>
                <td>{product.sku || product.id}</td>
                <td>{product.name}</td>
                <td>{product.categoryName || '—'}</td>
                {showDepartment ? <td>{product.departmentName || '—'}</td> : null}
                <td>{formatCurrency(product.price)}</td>
                <td>{product.availableQuantity ?? 0}</td>
                <td>
                  <StatusBadge status={product.status} />
                </td>
                <td>
                  {canRequest ? (
                    <Link
                      to={`/employee/purchase-requests/create?productId=${product.id}`}
                      className="btn btn-sm btn-primary"
                    >
                      Request
                    </Link>
                  ) : (
                    <button type="button" className="btn btn-sm btn-outline-secondary" disabled>
                      Unavailable
                    </button>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTable;
