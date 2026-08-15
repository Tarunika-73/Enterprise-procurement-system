import { useCallback, useEffect, useState } from 'react';
import { getVendorInvoices } from '../../services/vendorService';
import { formatCurrency, formatDate, getPageContent, getPageMeta } from '../../utils/employeeHelpers';
import { getApiErrorMessage } from '../../utils/apiErrors';

const VendorInvoicesPage = () => {
  const [invoices, setInvoices] = useState([]);
  const [meta, setMeta] = useState({ number: 0, totalPages: 0 });
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => {
    setLoading(true); setError('');
    try { const response = await getVendorInvoices({ page }); const data = response?.data ?? response; setInvoices(getPageContent(data)); setMeta(getPageMeta(data)); }
    catch (err) { setError(getApiErrorMessage(err, 'Unable to load invoices.')); setInvoices([]); }
    finally { setLoading(false); }
  }, [page]);
  useEffect(() => { load(); }, [load]);
  if (loading) return <div className="text-center py-5"><div className="spinner-border text-primary" /></div>;
  return <><div className="dashboard-page-header"><h1>Invoices</h1><p className="text-muted mb-0">Invoices generated from completed goods receipts.</p></div>{error && <div className="alert alert-danger">{error}</div>}{!error && invoices.length === 0 ? <div className="employee-table-card text-center py-5 text-muted">No invoices available yet.</div> : <div className="employee-table-card table-responsive"><table className="table employee-table align-middle mb-0"><thead><tr><th>Invoice Number</th><th>Purchase Order</th><th>Invoice Date</th><th>Due Date</th><th>Total Amount</th><th>Paid Amount</th><th>Balance</th><th>Status</th></tr></thead><tbody>{invoices.map((invoice) => <tr key={invoice.id}><td className="fw-semibold">{invoice.invoiceNumber}</td><td>{invoice.purchaseOrderNumber || '—'}</td><td>{formatDate(invoice.invoiceDate)}</td><td>{formatDate(invoice.dueDate)}</td><td>{formatCurrency(invoice.totalAmount)}</td><td>{formatCurrency(invoice.paidAmount)}</td><td>{formatCurrency(invoice.balanceAmount)}</td><td><span className={`badge text-bg-${invoice.status === 'PAID' ? 'success' : invoice.status === 'CANCELLED' ? 'secondary' : 'warning'}`}>{invoice.status?.replaceAll('_', ' ')}</span></td></tr>)}</tbody></table></div>}{meta.totalPages > 1 && <div className="d-flex justify-content-center gap-2 mt-3"><button className="btn btn-sm btn-outline-secondary" disabled={meta.number === 0} onClick={() => setPage((current) => current - 1)}>Previous</button><span className="align-self-center small text-muted">Page {meta.number + 1} of {meta.totalPages}</span><button className="btn btn-sm btn-outline-secondary" disabled={meta.number >= meta.totalPages - 1} onClick={() => setPage((current) => current + 1)}>Next</button></div>}</>;
};

export default VendorInvoicesPage;
