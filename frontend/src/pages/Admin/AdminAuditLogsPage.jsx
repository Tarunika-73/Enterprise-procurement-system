import { useCallback, useEffect, useState } from 'react';
import { getAdminAuditLogs } from '../../services/adminService';
import { formatDateTime, getPageContent, getPageMeta } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const AdminAuditLogsPage = () => {
  const [logs, setLogs] = useState([]);
  const [meta, setMeta] = useState({ totalPages: 0, number: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getAdminAuditLogs({ page, size: 20 });
      const payload = res?.data ?? res;
      setLogs(getPageContent(payload));
      setMeta(getPageMeta(payload));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load audit logs.'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const actionBadge = (action) => {
    const a = String(action || '').toUpperCase();
    if (a === 'INSERT' || a === 'CREATE') return 'bg-success';
    if (a === 'UPDATE') return 'bg-primary';
    if (a === 'DELETE') return 'bg-danger';
    return 'bg-secondary';
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Audit Logs</h1>
        <p className="text-muted mb-0">System-wide activity and change history.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
            </div>
          ) : logs.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-journal-text fs-1 d-block mb-2" />
              No audit logs found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Time</th>
                    <th>User</th>
                    <th>Action</th>
                    <th>Table</th>
                    <th>Record ID</th>
                    <th>Change</th>
                    <th>IP Address</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log) => (
                    <tr key={log.id}>
                      <td className="text-nowrap small">{formatDateTime(log.createdAt)}</td>
                      <td>{log.userName ?? <span className="text-muted">System</span>}</td>
                      <td>
                        <span className={`badge ${actionBadge(log.action)}`}>
                          {log.action}
                        </span>
                      </td>
                      <td><code className="small">{log.tableName}</code></td>
                      <td>{log.recordId}</td>
                      <td className="small" style={{ minWidth: '220px', whiteSpace: 'pre-wrap' }}>
                        {log.oldValue && <div><span className="text-muted">Before: </span>{log.oldValue}</div>}
                        {log.newValue && <div><span className="text-muted">After: </span>{log.newValue}</div>}
                        {!log.oldValue && !log.newValue && <span className="text-muted">—</span>}
                      </td>
                      <td className="small text-muted">{log.ipAddress ?? '—'}</td>
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
            Page {meta.number + 1} of {meta.totalPages} ({meta.totalElements} entries)
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

export default AdminAuditLogsPage;
