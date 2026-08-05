import api from './api';

const BASE = '/v1/reports';

/**
 * Role-scoped Reports page summary. Employees get their own activity,
 * Managers get their department, and Procurement Officers / Finance / Admin
 * get the whole organization — the backend decides the scope based on the
 * logged-in user.
 */
export const getReportSummary = () =>
  api.get(`${BASE}/summary`).then((r) => r.data);

/**
 * Downloads the current, role-scoped Reports page data as a CSV file and
 * triggers a browser save/download for it.
 */
export const downloadReport = async () => {
  const response = await api.get(`${BASE}/download`, { responseType: 'blob' });

  // Prefer the server-provided filename (from Content-Disposition) if present.
  const disposition = response.headers['content-disposition'];
  let filename = `report_${new Date().toISOString().slice(0, 10)}.csv`;
  const match = disposition && disposition.match(/filename\*?=(?:UTF-8'')?"?([^";]+)"?/i);
  if (match?.[1]) {
    filename = decodeURIComponent(match[1]);
  }

  const url = window.URL.createObjectURL(new Blob([response.data], { type: 'text/csv' }));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};
