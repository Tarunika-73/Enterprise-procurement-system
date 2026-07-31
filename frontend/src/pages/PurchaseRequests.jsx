import { useState } from "react";

const initialRequests = [
  {
    id: "PR001",
    department: "IT",
    employee: "Alice",
    amount: "₹65,000",
    priority: "High",
    status: "Approved",
  },
  {
    id: "PR002",
    department: "HR",
    employee: "Bob",
    amount: "₹12,000",
    priority: "Medium",
    status: "Approved",
  },
  {
    id: "PR003",
    department: "Finance",
    employee: "Charlie",
    amount: "₹8,500",
    priority: "Low",
    status: "Approved",
  },
  {
    id: "PR004",
    department: "Operations",
    employee: "David",
    amount: "₹1,25,000",
    priority: "High",
    status: "Approved",
  },
];

export default function PurchaseRequests() {
  const [requests] = useState(initialRequests);

  return (
    <div className="container-fluid mt-4">

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Purchase Requests</h2>

        <input
          type="text"
          className="form-control"
          placeholder="Search Request..."
          style={{ width: 260 }}
        />
      </div>

      <div className="row mb-4">

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>18</h3>
              <p className="text-muted">Approved Requests</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>6</h3>
              <p className="text-muted">Pending Orders</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>4</h3>
              <p className="text-muted">Urgent</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow border-0">
            <div className="card-body text-center">
              <h3>₹2.4L</h3>
              <p className="text-muted">Budget</p>
            </div>
          </div>
        </div>

      </div>

      <div className="card shadow">

        <div className="card-header bg-primary text-white">
          Approved Purchase Requests
        </div>

        <div className="card-body">

          <table className="table table-hover align-middle">

            <thead>

            <tr>

              <th>ID</th>

              <th>Department</th>

              <th>Requested By</th>

              <th>Amount</th>

              <th>Priority</th>

              <th>Status</th>

              <th>Action</th>

            </tr>

            </thead>

            <tbody>

            {requests.map((item)=>(

              <tr key={item.id}>

                <td>{item.id}</td>

                <td>{item.department}</td>

                <td>{item.employee}</td>

                <td>{item.amount}</td>

                <td>

                  <span className={`badge ${
                    item.priority==="High"
                    ? "bg-danger"
                    : item.priority==="Medium"
                    ? "bg-warning text-dark"
                    : "bg-success"
                  }`}>

                    {item.priority}

                  </span>

                </td>

                <td>

                  <span className="badge bg-success">

                    {item.status}

                  </span>

                </td>

                <td>

                  <button className="btn btn-outline-primary btn-sm me-2">
                    View
                  </button>

                  <button className="btn btn-success btn-sm">
                    Create PO
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