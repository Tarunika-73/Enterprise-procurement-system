import { useCallback, useEffect, useState } from 'react';
import { getMyNotifications } from '../../services/notificationService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { formatDateTime, getPageContent } from '../../utils/employeeHelpers';

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadNotifications = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getMyNotifications();
      setNotifications(getPageContent(response));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load notifications.'));
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Notifications</h1>
        <p className="text-muted mb-0">
          Updates related to your purchase requests and approvals.
        </p>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <div className="employee-table-card">
        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : notifications.length === 0 ? (
          <div className="text-center text-muted py-5">
            No notifications yet.
          </div>
        ) : (
          <ul className="list-group list-group-flush">
            {notifications.map((item) => (
              <li
                key={item.id}
                className={`list-group-item px-4 py-3 ${item.isRead ? '' : 'employee-notification-unread'}`}
              >
                <div className="d-flex justify-content-between gap-3 flex-wrap">
                  <strong>{item.subject}</strong>
                  <span className="text-muted small">{formatDateTime(item.createdAt)}</span>
                </div>
                <p className="mb-0 mt-1 text-muted">{item.message}</p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  );
};

export default NotificationsPage;
