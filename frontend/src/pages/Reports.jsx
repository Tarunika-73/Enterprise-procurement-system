const recentActivities = [
  {
    id: 1,
    activity: "Purchase Order PO001 Approved",
    time: "Today",
  },
  {
    id: 2,
    activity: "Vendor Dell Technologies Added",
    time: "Yesterday",
  },
  {
    id: 3,
    activity: "Budget Updated for IT Department",
    time: "2 Days Ago",
  },
];

export default function Reports() {
  return (
    <div className="container-fluid mt-4">

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Procurement Reports</h2>

        <button className="btn btn-success">
          Download Report
        </button>
      </div>

      {/* KPI Cards */}

      <div className="row mb-4">

        <div className="col-md-3">

          <div className="card shadow border-0">

            <div className="card-body text-center">

              <h2>152</h2>

              <p>Total Requests</p>

            </div>

          </div>

        </div>

        <div className="col-md-3">

          <div className="card shadow border-0">

            <div className="card-body text-center">

              <h2>98</h2>

              <p>Approved</p>

            </div>

          </div>

        </div>

        <div className="col-md-3">

          <div className="card shadow border-0">

            <div className="card-body text-center">

              <h2>41</h2>

              <p>Purchase Orders</p>

            </div>

          </div>

        </div>

        <div className="col-md-3">

          <div className="card shadow border-0">

            <div className="card-body text-center">

              <h2>28</h2>

              <p>Active Vendors</p>

            </div>

          </div>

        </div>

      </div>

      <div className="row">

        {/* Budget */}

        <div className="col-lg-6">

          <div className="card shadow mb-4">

            <div className="card-header bg-primary text-white">
              Budget Utilization
            </div>

            <div className="card-body">

              <h6>IT Department</h6>

              <div className="progress mb-3">

                <div
                  className="progress-bar bg-success"
                  style={{ width: "75%" }}
                >
                  75%
                </div>

              </div>

              <h6>Finance Department</h6>

              <div className="progress mb-3">

                <div
                  className="progress-bar bg-warning"
                  style={{ width: "58%" }}
                >
                  58%
                </div>

              </div>

              <h6>HR Department</h6>

              <div className="progress">

                <div
                  className="progress-bar bg-info"
                  style={{ width: "82%" }}
                >
                  82%
                </div>

              </div>

            </div>

          </div>

        </div>

        {/* Activity */}

        <div className="col-lg-6">

          <div className="card shadow mb-4">

            <div className="card-header bg-success text-white">
              Recent Activity
            </div>

            <div className="card-body">

              {recentActivities.map((item) => (

                <div
                  key={item.id}
                  className="d-flex justify-content-between border-bottom py-2"
                >

                  <span>{item.activity}</span>

                  <small className="text-muted">{item.time}</small>

                </div>

              ))}

            </div>

          </div>

        </div>

      </div>

    </div>
  );
}