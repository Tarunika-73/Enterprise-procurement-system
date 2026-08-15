import { useCallback, useEffect, useState } from 'react';
import {
  getAdminVendors,
  activateVendor,
  deactivateVendor,
} from '../../services/adminService';
import { formatDate, getPageContent, getPageMeta } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminVendorsPage = () => {
  const [vendors, setVendors] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminVendors({ page, size: 10 });
      const payload = res?.data ?? res;
      setVendors(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load vendors.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const handleActivate = async (id) => {
    setActionError('');
    try { await activateVendor(id); load(); }
    catch (err) { setActionError(getApiErrorMessage(err, 'Failed to activate vendor.')); }
  };

  const handleDeactivate = async (id) => {
    setActionError('');
    try { await deactivateVendor(id); load(); }
    catch (err) { setActionError(getApiErrorMessage(err, 'Failed to deactivate vendor.')); }
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Vendor Management</h1>
        <p className="text-muted mb-0">View and manage all registered vendors.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {actionError && <div className="alert alert-warning">{actionError}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : vendors.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-building fs-1 d-block mb-2" />
              No vendors found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Vendor Name</th>
                    <th>Contact</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>GST Number</th>
                    <th>Status</th>
                    <th>Registered</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {vendors.map((v) => (
                    <tr key={v.id}>
                      <td className="fw-semibold">{v.vendorName}</td>
                      <td>{v.contactName ?? '—'}</td>
                      <td>{v.email}</td>
                      <td>{v.phone ?? '—'}</td>
                      <td>{v.gstNumber ?? '—'}</td>
                      <td>
                        <span className={`badge ${v.isActive ? 'bg-success' : 'bg-danger'}`}>
                          {v.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td>{formatDate(v.createdAt)}</td>
                      <td>
                        <div className="d-flex gap-1">
                          {v.isActive ? (
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-warning"
                              title="Deactivate"
                              onClick={() => handleDeactivate(v.id)}
                            >
                              <i className="bi bi-pause-circle" />
                            </button>
                          ) : (
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-success"
                              title="Activate"
                              onClick={() => handleActivate(v.id)}
                            >
                              <i className="bi bi-play-circle" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {meta.totalPages > 1 && (
        <div className="d-flex justify-content-center gap-2 mt-3">
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            disabled={meta.number === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            Previous
          </button>
          <span className="align-self-center small text-muted">
            Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} vendors)
          </span>
          <button
            type="button"
            className="btn btn-sm btn-outline-secondary"
            disabled={meta.number >= meta.totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </>
  );
};

export default AdminVendorsPage;
