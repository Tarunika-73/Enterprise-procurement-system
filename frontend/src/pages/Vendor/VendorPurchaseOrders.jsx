const MOCK_PURCHASE_ORDERS = [
  { id: 'PO-2026-0142', item: 'Office Networking Equipment', quantity: 12, amount: '₹4,20,000', status: 'Issued', expectedDelivery: '2026-08-14' },
  { id: 'PO-2026-0138', item: 'Laptops — Dell Latitude 5440', quantity: 25, amount: '₹18,75,000', status: 'In Progress', expectedDelivery: '2026-08-05' },
  { id: 'PO-2026-0121', item: 'Ergonomic Office Chairs', quantity: 40, amount: '₹6,00,000', status: 'Delivered', expectedDelivery: '2026-07-18' },
  { id: 'PO-2026-0109', item: 'Server Rack Cooling Units', quantity: 6, amount: '₹9,60,000', status: 'Closed', expectedDelivery: '2026-06-30' },
];

const STATUS_BADGES = {
  Issued: 'primary',
  'In Progress': 'warning',
  Delivered: 'success',
  Closed: 'secondary',
};

const VendorPurchaseOrders = () => {
  return (
    <>
      <div className="dashboard-page-header d-flex flex-wrap align-items-start justify-content-between gap-2">
        <div>
          <h1>Purchase Orders</h1>
          <p className="text-muted mb-0">Purchase orders issued to you by the procurement team.</p>
        </div>
        <span className="dashboard-placeholder-tag">Preview — live data pending</span>
      </div>

      <div className="alert alert-info d-flex align-items-start gap-2" role="alert">
        <i className="bi bi-info-circle-fill mt-1" aria-hidden="true" />
        <span>
          The Purchase Order backend module is still being built out, so the table below shows sample data to
          preview the vendor experience. It will switch to live orders automatically once the API is connected.
        </span>
      </div>

      <div className="dashboard-panel p-0">
        <div className="table-responsive">
          <table className="table dashboard-table mb-0">
            <thead>
              <tr>
                <th>PO Number</th>
                <th>Item</th>
                <th>Quantity</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Expected Delivery</th>
              </tr>
            </thead>
            <tbody>
              {MOCK_PURCHASE_ORDERS.map((po) => (
                <tr key={po.id}>
                  <td className="fw-medium">{po.id}</td>
                  <td>{po.item}</td>
                  <td>{po.quantity}</td>
                  <td>{po.amount}</td>
                  <td>
                    <span className={`badge text-bg-${STATUS_BADGES[po.status] || 'secondary'}`}>{po.status}</span>
                  </td>
                  <td className="text-muted">
                    {new Date(po.expectedDelivery).toLocaleDateString(undefined, {
                      year: 'numeric',
                      month: 'short',
                      day: 'numeric',
                    })}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
};

export default VendorPurchaseOrders;
