import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getMyNotifications,
  markAsRead,
  markAllAsRead,
  deleteNotification,
} from '../../services/notificationService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { formatDateTime, getPageContent } from '../../utils/employeeHelpers';

const NotificationsPage = () => {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('all'); // 'all' | 'unread'
  const [actionLoading, setActionLoading] = useState(false);

  const loadNotifications = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const isReadParam = filter === 'unread' ? false : undefined;
      const response = await getMyNotifications({ isRead: isReadParam });
      setNotifications(getPageContent(response));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load notifications.'));
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  const handleMarkAsRead = async (id, e) => {
    e?.stopPropagation();
    try {
      await markAsRead(id);
      setNotifications((prev) =>
        prev.map((item) => (item.id === id ? { ...item, isRead: true } : item))
      );
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to mark notification as read.'));
    }
  };

  const handleMarkAllAsRead = async () => {
    setActionLoading(true);
    try {
      await markAllAsRead();
      setNotifications((prev) => prev.map((item) => ({ ...item, isRead: true })));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to mark all notifications as read.'));
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async (id, e) => {
    e?.stopPropagation();
    try {
      await deleteNotification(id);
      setNotifications((prev) => prev.filter((item) => item.id !== id));
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to delete notification.'));
    }
  };

  const handleOpen = async (item) => {
    if (!item.isRead) await handleMarkAsRead(item.id);
    if (item.referenceType === 'GOODS_RECEIPT_DELIVERY' && item.referenceId) {
      navigate(`/dashboard/goods-receipts?deliveryId=${item.referenceId}`);
    }
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return (
    <>
      <div className="dashboard-page-header d-flex justify-content-between align-items-center flex-wrap gap-3">
        <div>
          <h1>Notifications</h1>
          <p className="text-muted mb-0">
            Updates and alert notifications related to your purchase requests and workflows.
          </p>
        </div>
        <div className="d-flex align-items-center gap-2">
          <button
            type="button"
            className="btn btn-outline-primary btn-sm d-flex align-items-center gap-1"
            onClick={handleMarkAllAsRead}
            disabled={actionLoading || notifications.every((n) => n.isRead)}
          >
            <i className="bi bi-check2-all" />
            Mark all as read
          </button>
        </div>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <div className="d-flex gap-2 mb-3">
        <button
          type="button"
          className={`btn btn-sm ${filter === 'all' ? 'btn-primary' : 'btn-light'}`}
          onClick={() => setFilter('all')}
        >
          All
        </button>
        <button
          type="button"
          className={`btn btn-sm ${filter === 'unread' ? 'btn-primary' : 'btn-light'}`}
          onClick={() => setFilter('unread')}
        >
          Unread {unreadCount > 0 ? `(${unreadCount})` : ''}
        </button>
      </div>

      <div className="employee-table-card">
        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : notifications.length === 0 ? (
          <div className="text-center text-muted py-5">
            <i className="bi bi-bell-slash fs-2 d-block mb-2 text-secondary" />
            No notifications found.
          </div>
        ) : (
          <ul className="list-group list-group-flush">
            {notifications.map((item) => (
              <li
                key={item.id}
                className={`list-group-item px-4 py-3 ${
                  item.isRead ? '' : 'employee-notification-unread'
                } d-flex justify-content-between align-items-start gap-3`}
                style={{ cursor: item.referenceType === 'GOODS_RECEIPT_DELIVERY' || !item.isRead ? 'pointer' : 'default' }}
                onClick={() => handleOpen(item)}
              >
                <div className="flex-grow-1">
                  <div className="d-flex justify-content-between gap-3 flex-wrap">
                    <div className="d-flex align-items-center gap-2">
                      {!item.isRead && (
                        <span
                          className="bg-primary rounded-circle"
                          style={{ width: '8px', height: '8px', display: 'inline-block' }}
                          title="Unread"
                        />
                      )}
                      <strong>{item.subject}</strong>
                    </div>
                    <span className="text-muted small">{formatDateTime(item.createdAt)}</span>
                  </div>
                  <p className="mb-0 mt-1 text-muted">{item.message}</p>
                  {item.referenceType === 'GOODS_RECEIPT_DELIVERY' && <small className="text-primary d-block mt-2">Open goods receipt <i className="bi bi-arrow-right" /></small>}
                </div>

                <div className="d-flex align-items-center gap-1">
                  {!item.isRead && (
                    <button
                      type="button"
                      className="btn btn-sm text-secondary"
                      title="Mark as read"
                      onClick={(e) => handleMarkAsRead(item.id, e)}
                    >
                      <i className="bi bi-check2" />
                    </button>
                  )}
                  <button
                    type="button"
                    className="btn btn-sm text-danger"
                    title="Delete notification"
                    onClick={(e) => handleDelete(item.id, e)}
                  >
                    <i className="bi bi-trash" />
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  );
};

export default NotificationsPage;
