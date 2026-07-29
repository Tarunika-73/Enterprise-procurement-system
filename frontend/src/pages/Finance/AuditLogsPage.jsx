import { useState, useEffect } from 'react';
import DataTable from '../../components/finance/DataTable';
import financeService from '../../services/financeService';
import { exportToCSV } from '../../utils/exportUtils';

const AuditLogsPage = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    financeService.getAuditLogs().then((data) => {
      if (isMounted) {
        setLogs(data);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const columns = [
    { header: 'Log ID', key: 'id' },
    {
      header: 'User',
      key: 'user',
      render: (r) => (
        <div>
          <span className="fw-bold d-block text-dark">{r.user}</span>
          <span className="text-muted" style={{ fontSize: '0.75rem' }}>{r.role}</span>
        </div>
      ),
    },
    {
      header: 'Action',
      key: 'action',
      render: (r) => <span className="badge bg-light text-primary border">{r.action}</span>,
    },
    { header: 'Module', key: 'module' },
    { header: 'Date', key: 'date' },
    { header: 'Time', key: 'time' },
    { header: 'IP Address', key: 'ipAddress', render: (r) => <span className="font-monospace text-muted small">{r.ipAddress}</span> },
    { header: 'Description', key: 'description' },
  ];

  const filterOptions = [
    { label: 'Invoices', value: 'Invoices' },
    { label: 'Payments', value: 'Payments' },
    { label: 'Reports', value: 'Reports' },
  ];

  return (
    <div className="finance-audit-page container-fluid py-3">
      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">System Audit Logs</h4>
          <p className="text-muted small mb-0">
            Immutable log trail of all user actions, security approvals, and system transactions.
          </p>
        </div>

        <button
          className="btn btn-sm btn-outline-secondary"
          onClick={() => exportToCSV(logs, 'audit_logs.csv')}
        >
          <i className="bi bi-download me-1" /> Export Audit CSV
        </button>
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading audit trails...</p>
        </div>
      ) : (
        <DataTable
          columns={columns}
          data={logs}
          searchPlaceholder="Search audit logs by user, description, module..."
          filterKey="module"
          filterOptions={filterOptions}
        />
      )}
    </div>
  );
};

export default AuditLogsPage;
