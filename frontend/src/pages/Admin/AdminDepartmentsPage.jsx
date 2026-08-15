import { useCallback, useEffect, useState } from 'react';
import { getAdminDepartments } from '../../services/adminService';
import { formatDate, getPageContent, getPageMeta } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminDepartmentsPage = () => {
  const [departments, setDepartments] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminDepartments({ page, size: 20 });
      const payload = res?.data ?? res;
      setDepartments(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load departments.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Department Management</h1>
        <p className="text-muted mb-0">View all departments in the system.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : departments.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-building fs-1 d-block mb-2" />
              No departments found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>#</th>
                    <th>Department Name</th>
                    <th>Code</th>
                    <th>Manager</th>
                    <th>Created</th>
                  </tr>
                </thead>
                <tbody>
                  {departments.map((d, idx) => (
                    <tr key={d.id}>
                      <td className="text-muted">{idx + 1 + page * 20}</td>
                      <td className="fw-semibold">{d.name}</td>
                      <td><span className="badge bg-secondary">{d.code}</span></td>
                      <td>{d.managerName ?? <span className="text-muted">—</span>}</td>
                      <td>{formatDate(d.createdAt)}</td>
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
            Page {meta.number + 1} of {meta.totalPages}
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

export default AdminDepartmentsPage;
