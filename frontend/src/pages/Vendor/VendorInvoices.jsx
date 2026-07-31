import Button from '../../components/Authentication/Button/Button';

const MOCK_INVOICES = [
  { id: 'INV-2026-0087', poNumber: 'PO-2026-0121', amount: '₹6,00,000', status: 'Paid', submittedOn: '2026-07-20' },
  { id: 'INV-2026-0091', poNumber: 'PO-2026-0109', amount: '₹9,60,000', status: 'Approved', submittedOn: '2026-07-25' },
  { id: 'INV-2026-0094', poNumber: 'PO-2026-0138', amount: '₹9,37,500', status: 'Pending Review', submittedOn: '2026-07-28' },
];

const STATUS_BADGES = {
  Paid: 'success',
  Approved: 'primary',
  'Pending Review': 'warning',
  Rejected: 'danger',
};

const VendorInvoices = () => {
  return (
    <>
      <div className="dashboard-page-header d-flex flex-wrap align-items-start justify-content-between gap-2">
        <div>
          <h1>Invoices</h1>
          <p className="text-muted mb-0">Track invoices you've submitted against your purchase orders.</p>
        </div>
        <span className="dashboard-placeholder-tag">Preview — live data pending</span>
      </div>

      <div className="alert alert-info d-flex align-items-start gap-2" role="alert">
        <i className="bi bi-info-circle-fill mt-1" aria-hidden="true" />
        <span>
          The Invoice backend module is still being built out, so the table below shows sample data to preview
          the vendor experience. Invoice submission will be enabled once the API is connected.
        </span>
      </div>

      <div className="dashboard-panel p-0">
        <div className="d-flex justify-content-end p-3 pb-0">
          <Button type="button" fullWidth={false} disabled title="Coming soon — backend module in progress">
            <i className="bi bi-plus-lg me-2" aria-hidden="true" />
            Submit Invoice
          </Button>
        </div>
        <div className="table-responsive">
          <table className="table dashboard-table mb-0">
            <thead>
              <tr>
                <th>Invoice Number</th>
                <th>Linked PO</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Submitted On</th>
              </tr>
            </thead>
            <tbody>
              {MOCK_INVOICES.map((invoice) => (
                <tr key={invoice.id}>
                  <td className="fw-medium">{invoice.id}</td>
                  <td>{invoice.poNumber}</td>
                  <td>{invoice.amount}</td>
                  <td>
                    <span className={`badge text-bg-${STATUS_BADGES[invoice.status] || 'secondary'}`}>
                      {invoice.status}
                    </span>
                  </td>
                  <td className="text-muted">
                    {new Date(invoice.submittedOn).toLocaleDateString(undefined, {
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

export default VendorInvoices;
