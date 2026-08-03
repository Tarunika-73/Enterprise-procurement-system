import { useEffect, useState } from 'react';
import { getActiveVendors } from '../services/procurementService';
import { getPageContent, getPageMeta } from '../utils/employeeHelpers';
import { getApiErrorMessage } from '../utils/apiErrors';

export default function Suppliers() {
  const [vendors, setVendors] = useState([]);
  const [meta, setMeta] = useState({ totalElements: 0, totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  const load = (page = 0) => {
    setLoading(true);
    setError('');
    getActiveVendors({ page, size: 12 })
      .then((res) => {
        setVendors(getPageContent(res));
        setMeta(getPageMeta(res));
      })
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load vendors.')))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(0); }, []);

  const filtered = vendors.filter((v) => {
    const q = search.toLowerCase();
    return (
      !q ||
      v.vendorName?.toLowerCase().includes(q) ||
      v.email?.toLowerCase().includes(q) ||
      v.gstNumber?.toLowerCase().includes(q)
    );
  });

  return (
    <div className="container-fluid mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Vendor Management</h2>
        <input
          type="text"
          className="form-control"
          placeholder="Search by name, email, GST…"
          style={{ width: 280 }}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-5 text-muted">No active vendors found.</div>
      ) : (
        <>
          <div className="row">
            {filtered.map((v) => (
              <div className="col-lg-4 col-md-6 mb-4" key={v.id}>
                <div className="card shadow border-0 h-100">
                  <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h5 className="mb-0">{v.vendorName}</h5>
                      <span className={`badge ${v.isActive ? 'bg-success' : 'bg-danger'}`}>
                        {v.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </div>
                    <hr />
                    <p className="mb-1">
                      <i className="bi bi-envelope me-2 text-muted" />
                      {v.email || '—'}
                    </p>
                    <p className="mb-1">
                      <i className="bi bi-telephone me-2 text-muted" />
                      {v.phone || '—'}
                    </p>
                    <p className="mb-1">
                      <i className="bi bi-receipt me-2 text-muted" />
                      <strong>GST:</strong> {v.gstNumber || '—'}
                    </p>
                    {v.contactName && (
                      <p className="mb-0">
                        <i className="bi bi-person me-2 text-muted" />
                        {v.contactName}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {meta.totalPages > 1 && (
            <div className="d-flex justify-content-between align-items-center mt-2">
              <small className="text-muted">Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} vendors)</small>
              <div className="btn-group btn-group-sm">
                <button
                  className="btn btn-outline-secondary"
                  disabled={meta.number === 0}
                  onClick={() => load(meta.number - 1)}
                >
                  Previous
                </button>
                <button
                  className="btn btn-outline-secondary"
                  disabled={meta.number + 1 >= meta.totalPages}
                  onClick={() => load(meta.number + 1)}
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
