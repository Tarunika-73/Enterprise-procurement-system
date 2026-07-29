import { useState, useEffect } from 'react';
import financeService from '../../services/financeService';

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    financeService.getNotifications().then((data) => {
      if (isMounted) {
        setNotifications(data);
        setLoading(false);
      }
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const handleMarkRead = async (id) => {
    await financeService.markNotificationRead(id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
    );
  };

  const handleMarkAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  const handleDelete = (id) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return (
    <div className="finance-notifications-page container-fluid py-3">
      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">
            Notifications & System Alerts{' '}
            {unreadCount > 0 && <span className="badge bg-danger ms-2">{unreadCount} Unread</span>}
          </h4>
          <p className="text-muted small mb-0">
            Real-time updates regarding invoice approvals, disbursements, and system alerts.
          </p>
        </div>

        {unreadCount > 0 && (
          <button className="btn btn-sm btn-outline-primary" onClick={handleMarkAllRead}>
            <i className="bi bi-check-all me-1" /> Mark All as Read
          </button>
        )}
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Loading notifications...</p>
        </div>
      ) : (
        <div className="card border-0 shadow-sm p-3">
          {notifications.length > 0 ? (
            <div className="list-group list-group-flush">
              {notifications.map((item) => {
                const iconClass =
                  item.type === 'success'
                    ? 'bi-check-circle-fill text-success'
                    : item.type === 'warning'
                    ? 'bi-exclamation-triangle-fill text-warning'
                    : item.type === 'info'
                    ? 'bi-info-circle-fill text-info'
                    : 'bi-bell-fill text-primary';

                return (
                  <div
                    key={item.id}
                    className={`list-group-item px-3 py-3 border-bottom d-flex align-items-start justify-content-between gap-3 ${
                      !item.isRead ? 'bg-light' : ''
                    }`}
                  >
                    <div className="d-flex align-items-start gap-3">
                      <i className={`bi ${iconClass} fs-4 mt-1`} />
                      <div>
                        <div className="d-flex align-items-center gap-2">
                          <h6 className="fw-bold text-dark mb-1">{item.title}</h6>
                          {!item.isRead && (
                            <span className="badge bg-primary rounded-pill" style={{ fontSize: '0.65rem' }}>
                              New
                            </span>
                          )}
                        </div>
                        <p className="text-secondary small mb-1">{item.message}</p>
                        <span className="text-muted" style={{ fontSize: '0.75rem' }}>
                          {item.timestamp}
                        </span>
                      </div>
                    </div>

                    <div className="d-flex align-items-center gap-2 ms-auto">
                      {!item.isRead && (
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-secondary"
                          title="Mark as Read"
                          onClick={() => handleMarkRead(item.id)}
                        >
                          <i className="bi bi-check" />
                        </button>
                      )}
                      <button
                        type="button"
                        className="btn btn-sm btn-outline-danger"
                        title="Delete Notification"
                        onClick={() => handleDelete(item.id)}
                      >
                        <i className="bi bi-trash" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-bell-slash fs-1 d-block mb-2 opacity-50" />
              <span>No notifications in your inbox</span>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationsPage;
