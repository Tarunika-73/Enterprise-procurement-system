const history = [
  {
    id: "PR001",
    employee: "Alice",
    action: "Approved",
    date: "31 Jul 2026",
    remarks: "Budget Approved",
  },
  {
    id: "PR002",
    employee: "Bob",
    action: "Rejected",
    date: "30 Jul 2026",
    remarks: "Insufficient Budget",
  },
  {
    id: "PR003",
    employee: "Charlie",
    action: "Approved",
    date: "29 Jul 2026",
    remarks: "Urgent Requirement",
  },
];

export default function ApprovalHistory() {
  return (
    <div className="container mt-4">
      <h2 className="mb-4">Approval History</h2>

      <table className="table table-striped table-hover shadow">
        <thead className="table-dark">
          <tr>
            <th>Request ID</th>
            <th>Employee</th>
            <th>Decision</th>
            <th>Date</th>
            <th>Remarks</th>
          </tr>
        </thead>

        <tbody>
          {history.map((item) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.employee}</td>

              <td>
                <span
                  className={`badge ${
                    item.action === "Approved"
                      ? "bg-success"
                      : "bg-danger"
                  }`}
                >
                  {item.action}
                </span>
              </td>

              <td>{item.date}</td>

              <td>{item.remarks}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}