const orders = [
  {
    id: "PO001",
    vendor: "Dell Technologies",
    amount: "₹65,000",
    delivery: "05 Aug 2026",
    status: "Processing",
  },
  {
    id: "PO002",
    vendor: "HP India",
    amount: "₹1,20,000",
    delivery: "02 Aug 2026",
    status: "In Transit",
  },
  {
    id: "PO003",
    vendor: "Lenovo",
    amount: "₹45,000",
    delivery: "28 Jul 2026",
    status: "Delivered",
  },
  {
    id: "PO004",
    vendor: "Canon",
    amount: "₹18,500",
    delivery: "09 Aug 2026",
    status: "Processing",
  },
];

export default function PurchaseOrders() {
  return (
    <div className="container-fluid mt-4">

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Purchase Orders</h2>

        <button className="btn btn-primary">
          + Create Purchase Order
        </button>
      </div>

      <div className="row mb-4">

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>20</h3>
              <p className="text-muted">Total Orders</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>8</h3>
              <p className="text-muted">Delivered</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>6</h3>
              <p className="text-muted">Processing</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>6</h3>
              <p className="text-muted">In Transit</p>
            </div>
          </div>
        </div>

      </div>

      <div className="card shadow">

        <div className="card-header bg-success text-white">
          Purchase Orders
        </div>

        <div className="card-body">

          <table className="table table-hover align-middle">

            <thead>

              <tr>
                <th>PO ID</th>
                <th>Vendor</th>
                <th>Amount</th>
                <th>Delivery Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>

            </thead>

            <tbody>

              {orders.map((order) => (

                <tr key={order.id}>

                  <td>{order.id}</td>

                  <td>{order.vendor}</td>

                  <td>{order.amount}</td>

                  <td>{order.delivery}</td>

                  <td>

                    <span
                      className={`badge ${
                        order.status === "Delivered"
                          ? "bg-success"
                          : order.status === "Processing"
                          ? "bg-warning text-dark"
                          : "bg-primary"
                      }`}
                    >
                      {order.status}
                    </span>

                  </td>

                  <td>

                    <button className="btn btn-outline-primary btn-sm me-2">
                      View
                    </button>

                    <button className="btn btn-success btn-sm">
                      Track
                    </button>

                  </td>

                </tr>

              ))}

            </tbody>

          </table>

        </div>

      </div>

    </div>
  );
}