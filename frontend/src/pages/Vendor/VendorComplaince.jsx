import { useEffect, useState } from 'react';
import useVendorProfile from '../../hooks/useVendorProfile';
import { getVendorCompliance } from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import Loader from '../../components/Authentication/Loader/Loader';

const STATUS_BADGES = {
  COMPLIANT: 'success',
  NON_COMPLIANT: 'danger',
  UNDER_REVIEW: 'warning',
};

const formatStatus = (status) => {
  if (!status) return 'Unknown';
  return status.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
};

const formatDate = (value) => {
  if (!value) return '—';
  return new Date(value).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
};

const isExpiringSoon = (expiryDate) => {
  if (!expiryDate) return false;
  const now = new Date();
  const soon = new Date();
  soon.setDate(now.getDate() + 30);
  const expiry = new Date(expiryDate);
  return expiry >= now && expiry <= soon;
};

const isExpired = (expiryDate) => {
  if (!expiryDate) return false;
  return new Date(expiryDate) < new Date();
};

const VendorCompliance = () => {
  const { vendor, isLoading: vendorLoading, error: vendorError } = useVendorProfile();
  const [records, setRecords] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!vendor?.id) {
      setIsLoading(false);
      return;
    }

    let cancelled = false;
    setIsLoading(true);

    getVendorCompliance(vendor.id, { page: 0, size: 50, sort: 'expiryDate,asc' })
      .then((response) => {
        if (cancelled) return;
        const page = response?.data ?? response;
        setRecords(page?.content ?? []);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(getApiErrorMessage(err, 'Unable to load your compliance records.'));
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [vendor?.id]);

  if (vendorLoading) {
    return (
      <div className="d-flex align-items-center gap-2 text-muted">
        <Loader size="sm" />
        <span>Loading vendor details…</span>
      </div>
    );
  }

  if (vendorError || !vendor) {
    return (
      <div className="alert alert-warning d-flex align-items-center gap-2" role="alert">
        <i className="bi bi-exclamation-triangle-fill" aria-hidden="true" />
        <span>{vendorError || 'No vendor profile found for this account.'}</span>
      </div>
    );
  }

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Compliance Documents</h1>
        <p className="text-muted mb-0">
          Certification and compliance documents your procurement team has on file for {vendor.vendorName}.
        </p>
      </div>

      {error && (
        <div className="alert alert-danger d-flex align-items-center gap-2" role="alert">
          <i className="bi bi-exclamation-triangle-fill" aria-hidden="true" />
          <span>{error}</span>
        </div>
      )}

      <div className="dashboard-panel p-0">
        {isLoading ? (
          <div className="d-flex align-items-center gap-2 text-muted p-4">
            <Loader size="sm" />
            <span>Loading compliance records…</span>
          </div>
        ) : records.length === 0 ? (
          <div className="dashboard-empty-state">
            <i className="bi bi-file-earmark-check" aria-hidden="true" />
            <p className="mb-0">No compliance documents on file yet.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table dashboard-table mb-0">
              <thead>
                <tr>
                  <th>Document Type</th>
                  <th>Status</th>
                  <th>Expiry Date</th>
                  <th>Last Updated</th>
                </tr>
              </thead>
              <tbody>
                {records.map((record) => (
                  <tr key={record.id}>
                    <td className="fw-medium">{record.documentType}</td>
                    <td>
                      <span className={`badge text-bg-${STATUS_BADGES[record.status] || 'secondary'}`}>
                        {formatStatus(record.status)}
                      </span>
                    </td>
                    <td>
                      {formatDate(record.expiryDate)}
                      {isExpired(record.expiryDate) && (
                        <span className="badge text-bg-danger ms-2">Expired</span>
                      )}
                      {!isExpired(record.expiryDate) && isExpiringSoon(record.expiryDate) && (
                        <span className="badge text-bg-warning ms-2">Expiring Soon</span>
                      )}
                    </td>
                    <td className="text-muted">{formatDate(record.updatedAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
};

export default VendorCompliance;
