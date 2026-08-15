import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import RequestTable from '../../components/employee/RequestTable';
import { getMyPurchaseRequests } from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getPageContent, getPageMeta } from '../../utils/employeeHelpers';

const MyRequestsPage = () => {
  const navigate = useNavigate();
  const [requests, setRequests] = useState([]);
  const [page, setPage] = useState(0);
  const [pageMeta, setPageMeta] = useState({ totalPages: 0, totalElements: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadRequests = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getMyPurchaseRequests({ page, size: 10 });
      setRequests(getPageContent(response));
      setPageMeta(getPageMeta(response));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load your purchase requests.'));
      setRequests([]);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    loadRequests();
  }, [loadRequests]);

  return (
    <>
      <div className="dashboard-page-header d-flex flex-wrap justify-content-between gap-3">
        <div>
          <h1>My Purchase Requests</h1>
          <p className="text-muted mb-0">
            Track status, approver, and remarks for every request you submitted.
          </p>
        </div>
        <Link to="/employee/purchase-requests/create" className="btn btn-primary align-self-start">
          Create Request
        </Link>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <RequestTable
        requests={requests}
        loading={loading}
        onViewDetails={(id) => navigate(`/employee/purchase-requests/${id}`)}
        onEdit={(id) => navigate(`/employee/purchase-requests/create?edit=${id}`)}
      />

      <div className="d-flex justify-content-between align-items-center mt-3">
        <span className="text-muted small">
          {pageMeta.totalElements} request{pageMeta.totalElements === 1 ? '' : 's'}
        </span>
        <div className="btn-group">
          <button
            type="button"
            className="btn btn-outline-secondary btn-sm"
            disabled={page <= 0 || loading}
            onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          >
            Previous
          </button>
          <button type="button" className="btn btn-outline-secondary btn-sm" disabled>
            Page {pageMeta.number + 1} / {Math.max(pageMeta.totalPages, 1)}
          </button>
          <button
            type="button"
            className="btn btn-outline-secondary btn-sm"
            disabled={page + 1 >= pageMeta.totalPages || loading}
            onClick={() => setPage((prev) => prev + 1)}
          >
            Next
          </button>
        </div>
      </div>
    </>
  );
};

export default MyRequestsPage;
