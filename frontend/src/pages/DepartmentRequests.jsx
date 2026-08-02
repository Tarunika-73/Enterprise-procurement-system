import { useState } from "react";

const sampleRequests = [
  {
    id: "PR001",
    employee: "Alice",
    item: "Laptop",
    amount: "₹65,000",
    status: "Pending",
  },
  {
    id: "PR002",
    employee: "Bob",
    item: "Office Chair",
    amount: "₹8,500",
    status: "Pending",
  },
  {
    id: "PR003",
    employee: "Charlie",
    item: "Printer",
    amount: "₹18,000",
    status: "Approved",
  },
];

export default function DepartmentRequests() {
  const [requests, setRequests] = useState(sampleRequests);

  const updateStatus = (id, status) => {
    setRequests((prev) =>
      prev.map((req) =>
        req.id === id ? { ...req, status } : req
      )
    );
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Department Requests</h2>

        <input
          className="form-control"
          placeholder="Search Request..."
          style={{ width: "250px" }}
        />
      </div>

      <table className="table table-bordered table-hover shadow-sm">
        <thead className="table-dark">
          <tr>
            <th>Request ID</th>
            <th>Employee</th>
            <th>Item</th>
            <th>Amount</th>
            <th>Status</th>
            <th width="250">Actions</th>
          </tr>
        </thead>

        <tbody>
          {requests.map((req) => (
            <tr key={req.id}>
              <td>{req.id}</td>
              <td>{req.employee}</td>
              <td>{req.item}</td>
              <td>{req.amount}</td>

              <td>
                <span
                  className={`badge ${
                    req.status === "Approved"
                      ? "bg-success"
                      : req.status === "Rejected"
                      ? "bg-danger"
                      : "bg-warning text-dark"
                  }`}
                >
                  {req.status}
                </span>
              </td>

              <td>
                <button className="btn btn-info btn-sm me-2">
                  View
                </button>

                {req.status === "Pending" && (
                  <>
                    <button
                      className="btn btn-success btn-sm me-2"
                      onClick={() =>
                        updateStatus(req.id, "Approved")
                      }
                    >
                      Approve
                    </button>

                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() =>
                        updateStatus(req.id, "Rejected")
                      }
                    >
                      Reject
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}