const vendors = [
  {
    id: 1,
    name: "Dell Technologies",
    rating: "★★★★★",
    email: "sales@dell.com",
    phone: "+91 9876543210",
    gst: "29ABCDE1234F1Z5",
    status: "Active",
  },
  {
    id: 2,
    name: "HP India",
    rating: "★★★★☆",
    email: "contact@hp.com",
    phone: "+91 9123456780",
    gst: "27HPIND1234A1Z6",
    status: "Active",
  },
  {
    id: 3,
    name: "Canon Pvt Ltd",
    rating: "★★★★★",
    email: "info@canon.in",
    phone: "+91 9988776655",
    gst: "36CANON5678X1Z8",
    status: "Inactive",
  },
];

export default function Suppliers() {
  return (
    <div className="container-fluid mt-4">

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Vendor Management</h2>

        <button className="btn btn-primary">
          + Add Vendor
        </button>
      </div>

      <div className="row">

        {vendors.map((vendor) => (

          <div className="col-lg-4 mb-4" key={vendor.id}>

            <div className="card shadow border-0 h-100">

              <div className="card-body">

                <h4>{vendor.name}</h4>

                <p className="text-warning fs-5">
                  {vendor.rating}
                </p>

                <hr />

                <p><strong>Email:</strong> {vendor.email}</p>

                <p><strong>Phone:</strong> {vendor.phone}</p>

                <p><strong>GST:</strong> {vendor.gst}</p>

                <p>

                  <strong>Status:</strong>{" "}

                  <span
                    className={`badge ${
                      vendor.status === "Active"
                        ? "bg-success"
                        : "bg-danger"
                    }`}
                  >
                    {vendor.status}
                  </span>

                </p>

              </div>

              <div className="card-footer bg-white border-0">

                <button className="btn btn-outline-primary w-100">
                  View Vendor Profile
                </button>

              </div>

            </div>

          </div>

        ))}

      </div>

    </div>
  );
}