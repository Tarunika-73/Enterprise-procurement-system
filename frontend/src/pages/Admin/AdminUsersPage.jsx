import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import {
  getAdminUsers,
  activateUser,
  deactivateUser,
  deleteUser,
} from '../../services/adminService';
import { formatDate, getPageContent, getPageMeta } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminUsersPage = () => {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminUsers({ page, size: 10 });
      const payload = res?.data ?? res;
      setUsers(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load users.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const handleActivate = async (id) => {
    setActionError('');
    try {
      await activateUser(id);
      load();
    } catch (err) {
      setActionError(getApiErrorMessage(err, 'Failed to activate user.'));
    }
  };

  const handleDeactivate = async (id) => {
    setActionError('');
    try {
      await deactivateUser(id);
      load();
    } catch (err) {
      setActionError(getApiErrorMessage(err, 'Failed to deactivate user.'));
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Soft-delete this user? They will no longer be able to log in.')) return;
    setActionError('');
    try {
      await deleteUser(id);
      load();
    } catch (err) {
      setActionError(getApiErrorMessage(err, 'Failed to delete user.'));
    }
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>User Management</h1>
        <p className="text-muted mb-0">View and manage all system users.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {actionError && <div className="alert alert-warning">{actionError}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : users.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-people fs-1 d-block mb-2" />
              No users found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Employee ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Department</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => {
                    const isSelf = u.id === currentUser?.id;
                    return (
                      <tr key={u.id}>
                        <td className="fw-semibold">{u.employeeId}</td>
                        <td>{u.firstName} {u.lastName}</td>
                        <td>{u.email}</td>
                        <td><span className="badge bg-secondary">{u.roleName}</span></td>
                        <td>{u.departmentName ?? '—'}</td>
                        <td>
                          <span className={`badge ${u.isActive ? 'bg-success' : 'bg-danger'}`}>
                            {u.isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td>{formatDate(u.createdAt)}</td>
                        <td>
                          <div className="d-flex gap-1">
                            {!isSelf && u.isActive && (
                              <button
                                type="button"
                                className="btn btn-sm btn-outline-warning"
                                title="Deactivate"
                                onClick={() => handleDeactivate(u.id)}
                              >
                                <i className="bi bi-person-x" />
                              </button>
                            )}
                            {!isSelf && !u.isActive && (
                              <button
                                type="button"
                                className="btn btn-sm btn-outline-success"
                                title="Activate"
                                onClick={() => handleActivate(u.id)}
                              >
                                <i className="bi bi-person-check" />
                              </button>
                            )}
                            {!isSelf && (
                              <button
                                type="button"
                                className="btn btn-sm btn-outline-danger"
                                title="Delete"
                                onClick={() => handleDelete(u.id)}
                              >
                                <i className="bi bi-trash" />
                              </button>
                            )}
                            {isSelf && (
                              <span className="text-muted small">You</span>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
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
            Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} users)
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

export default AdminUsersPage;
