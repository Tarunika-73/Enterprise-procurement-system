import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { USER_ROLES } from "../utils/constants";
import { normalizeRole } from "../utils/roleNavigation";
import { getReportSummary, downloadReport } from "../services/reportService";
import { formatCurrency, formatDateTime, unwrapApiData } from "../utils/employeeHelpers";
import { getApiErrorMessage } from "../utils/apiErrors";
import DashboardStatCard from '../components/dashboard/DashboardStatCard';
import { Cell, Legend, Line, LineChart, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

const ROLE_COPY = {
  [USER_ROLES.EMPLOYEE]: {
    title: "My Reports",
    subtitle: "A summary of your purchase request activity.",
  },
  [USER_ROLES.MANAGER]: {
    title: "Team Reports",
    subtitle: "Approvals, spend, and activity for your department.",
  },
  [USER_ROLES.PROCUREMENT_OFFICER]: {
    title: "Procurement Reports",
    subtitle: "Purchase requests, orders, and vendor activity across the organization.",
  },
  [USER_ROLES.FINANCE]: {
    title: "Finance Reports",
    subtitle: "Spend and activity across the organization.",
  },
  [USER_ROLES.ADMIN]: {
    title: "Organization Reports",
    subtitle: "A high-level view across all departments.",
  },
};

const DEFAULT_COPY = {
  title: "Reports",
  subtitle: "An overview of recent procurement activity.",
};

const PROGRESS_COLORS = ["bg-success", "bg-info", "bg-warning", "bg-primary", "bg-danger", "bg-secondary"];
const CHART_COLORS = ['#7C6FD6', '#6EB5FF', '#198754', '#f0ad4e', '#dc3545', '#6c757d'];

export default function Reports() {
  const { userRole } = useAuth();
  const role = normalizeRole(userRole);
  const { title, subtitle } = ROLE_COPY[role] ?? DEFAULT_COPY;

  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [downloading, setDownloading] = useState(false);
  const [downloadError, setDownloadError] = useState("");
  const [selectedMetric, setSelectedMetric] = useState('total');

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await getReportSummary();
      setSummary(unwrapApiData(res));
    } catch (err) {
      setError(getApiErrorMessage(err, "Failed to load reports."));
      setSummary(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleDownload = useCallback(async () => {
    setDownloading(true);
    setDownloadError("");
    try {
      await downloadReport();
    } catch (err) {
      setDownloadError(getApiErrorMessage(err, "Failed to download report."));
    } finally {
      setDownloading(false);
    }
  }, []);

  const departmentBreakdown = summary?.departmentBreakdown ?? [];
  const recentActivity = summary?.recentActivity ?? [];
  const statusData = Object.entries(summary?.requestStatusBreakdown ?? {}).map(([name, value]) => ({
    name: name.replaceAll('_', ' '), value,
  }));
  const monthlySpend = summary?.monthlySpend ?? [];
  const reportRows = selectedMetric === 'orders'
    ? (summary?.purchaseOrderRows ?? [])
    : (summary?.requestRows ?? []).filter((row) => selectedMetric === 'total' || row.status === selectedMetric.toUpperCase());
  const reportCards = [
    { key: 'total', icon: 'bi-file-earmark-text', iconVariant: 'primary', value: summary?.totalRequests ?? 0, label: 'Total Requests' },
    { key: 'approved', icon: 'bi-check-circle', iconVariant: 'success', value: summary?.approvedRequests ?? 0, label: 'Approved' },
    { key: 'orders', icon: 'bi-cart-check', iconVariant: 'info', value: summary?.purchaseOrders ?? 0, label: 'Purchase Orders' },
    { key: 'rejected', icon: 'bi-x-circle', iconVariant: 'danger', value: summary?.rejectedRequests ?? 0, label: 'Rejected' },
  ];

  return (
    <div className="container-fluid mt-4">

      <div className="d-flex justify-content-between align-items-center mb-1">
        <h2>{title}</h2>

        <div className="d-flex gap-2">
          <button
            className="btn btn-outline-primary"
            onClick={handleDownload}
            disabled={downloading || loading || !summary}
          >
            {downloading ? "Downloading…" : "Download Report"}
          </button>
          <button className="btn btn-success" onClick={load} disabled={loading}>
            {loading ? "Refreshing…" : "Refresh"}
          </button>
        </div>
      </div>

      <p className="text-muted mb-4">{subtitle}</p>

      {error && <div className="alert alert-danger">{error}</div>}
      {downloadError && <div className="alert alert-danger">{downloadError}</div>}

      {loading && !summary ? (
        <div className="d-flex justify-content-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading…</span>
          </div>
        </div>
      ) : (
        <>
          <div className="row g-3 mb-4">{reportCards.map((card) => <div className="col-12 col-sm-6 col-xl-3" key={card.key}><DashboardStatCard {...card} active={selectedMetric === card.key} onClick={() => setSelectedMetric(card.key)} /></div>)}</div>

          <div className="card shadow-sm mb-4"><div className="card-body p-0"><div className="px-3 pt-3"><h3 className="h6 mb-3">{reportCards.find((card) => card.key === selectedMetric)?.label}</h3></div><div className="table-responsive"><table className="table table-hover align-middle mb-0"><thead className="table-light"><tr><th>Reference</th><th>Title</th><th>Status</th><th>Amount</th><th>Created</th></tr></thead><tbody>{reportRows.length === 0 ? <tr><td colSpan="5" className="text-center text-muted py-4">No data available for this selection.</td></tr> : reportRows.map((row) => <tr key={`${row.reference}-${row.createdAt}`}><td><code>{row.reference}</code></td><td>{row.title || '—'}</td><td>{String(row.status).replaceAll('_', ' ')}</td><td>{formatCurrency(row.amount)}</td><td>{formatDateTime(row.createdAt)}</td></tr>)}</tbody></table></div></div></div>

          <div className="row">

            <div className="col-lg-6">
              <div className="card shadow mb-4 report-chart-card">
                <div className="card-header bg-primary text-white">Purchase Request Status Distribution</div>
                <div className="card-body">
                  {statusData.length === 0 ? <p className="text-muted mb-0">No purchase request activity yet.</p> : (
                    <div className="report-chart">
                      <ResponsiveContainer width="100%" height={280}>
                        <PieChart>
                          <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={55} outerRadius={90} paddingAngle={2}>
                            {statusData.map((entry, index) => <Cell key={entry.name} fill={CHART_COLORS[index % CHART_COLORS.length]} />)}
                          </Pie>
                          <Tooltip formatter={(value) => [value, 'Requests']} />
                          <Legend />
                        </PieChart>
                      </ResponsiveContainer>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="col-lg-6">
              <div className="card shadow mb-4 report-chart-card">
                <div className="card-header bg-success text-white">Monthly Procurement Spending</div>
                <div className="card-body">
                  {monthlySpend.length === 0 ? <p className="text-muted mb-0">No procurement spending data yet.</p> : (
                    <div className="report-chart">
                      <ResponsiveContainer width="100%" height={280}>
                        <LineChart data={monthlySpend} margin={{ top: 10, right: 20, left: 10, bottom: 5 }}>
                          <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                          <YAxis tickFormatter={(value) => `₹${Number(value).toLocaleString('en-IN')}`} width={75} tick={{ fontSize: 12 }} />
                          <Tooltip formatter={(value) => [formatCurrency(value), 'Spend']} />
                          <Legend />
                          <Line type="monotone" dataKey="amount" name="Procurement spend" stroke="#7C6FD6" strokeWidth={3} dot={{ r: 4 }} activeDot={{ r: 6 }} />
                        </LineChart>
                      </ResponsiveContainer>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Spend by Department */}

            <div className="col-lg-6">
              <div className="card shadow mb-4">
                <div className="card-header bg-primary text-white">
                  {departmentBreakdown.length > 1 ? "Spend by Department" : "Department Spend"}
                </div>

                <div className="card-body">
                  {departmentBreakdown.length === 0 ? (
                    <p className="text-muted mb-0">No purchase request activity yet.</p>
                  ) : (
                    departmentBreakdown.map((dept, idx) => (
                      <div key={dept.departmentId ?? dept.departmentName} className={idx > 0 ? "mt-3" : ""}>
                        <div className="d-flex justify-content-between">
                          <h6 className="mb-1">{dept.departmentName}</h6>
                          <small className="text-muted">
                            {formatCurrency(dept.totalSpend)} · {dept.requestCount} request
                            {dept.requestCount === 1 ? "" : "s"}
                          </small>
                        </div>
                        <div className="progress">
                          <div
                            className={`progress-bar ${PROGRESS_COLORS[idx % PROGRESS_COLORS.length]}`}
                            style={{ width: `${Math.max(dept.relativePercent, dept.totalSpend > 0 ? 4 : 0)}%` }}
                          />
                        </div>
                      </div>
                    ))
                  )}
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
                  {recentActivity.length === 0 ? (
                    <p className="text-muted mb-0">No recent activity.</p>
                  ) : (
                    recentActivity.map((item, idx) => (
                      <div
                        key={`${item.description}-${idx}`}
                        className="d-flex justify-content-between border-bottom py-2"
                      >
                        <span>{item.description}</span>
                        <small className="text-muted">{formatDateTime(item.timestamp)}</small>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>

          </div>
        </>
      )}

    </div>
  );
}
