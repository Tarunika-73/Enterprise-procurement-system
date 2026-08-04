import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import { getUnreadCount } from '../../../services/notificationService';
import { getDisplayName, getUserInitials, formatRoleLabel } from '../../../utils/userDisplay';
import { normalizeRole } from '../../../utils/roleNavigation';
import { USER_ROLES } from '../../../utils/constants';

const TopNavbar = ({ pageTitle, onToggleSidebar }) => {
  const navigate = useNavigate();
  const { user, userRole, logout } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);

  const displayName = getDisplayName(user);
  const initials    = getUserInitials(user);
  const roleLabel   = formatRoleLabel(userRole);
  const isVendor    = normalizeRole(userRole) === USER_ROLES.VENDOR;

  useEffect(() => {
    let isMounted = true;
    const fetchUnread = async () => {
      try {
        const response = await getUnreadCount();
        if (isMounted && response?.data?.unreadCount !== undefined) {
          setUnreadCount(response.data.unreadCount);
        }
      } catch (err) {
        // Silently handle if unauthenticated or error
      }
    };
    fetchUnread();
    return () => {
      isMounted = false;
    };
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleProfile = () => {
    navigate(isVendor ? '/vendor/profile' : '#');
  };

  return (
    <header className="dashboard-navbar">
      <div className="d-flex align-items-center justify-content-between gap-3">
        <div className="d-flex align-items-center gap-3">
          <button
            type="button"
            className="dashboard-navbar-btn d-lg-none"
            onClick={onToggleSidebar}
            aria-label="Toggle navigation menu"
          >
            <i className="bi bi-list fs-5" />
          </button>
          <h1 className="dashboard-navbar-title">{pageTitle}</h1>
        </div>

        <div className="d-flex align-items-center gap-2 gap-md-3">
          <button
            type="button"
            className="dashboard-navbar-btn position-relative"
            aria-label="Notifications"
            title="Notifications"
            onClick={() => navigate('/notifications')}
          >
            <i className="bi bi-bell" />
            {unreadCount > 0 && (
              <span
                className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                style={{ fontSize: '0.65rem' }}
              >
                {unreadCount > 99 ? '99+' : unreadCount}
                <span className="visually-hidden">unread notifications</span>
              </span>
            )}
          </button>

          <div className="dropdown dashboard-user-dropdown">
            <button
              className="btn dropdown-toggle d-flex align-items-center gap-2"
              type="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
              id="userProfileDropdown"
            >
              <span className="dashboard-user-avatar" aria-hidden="true">
                {initials}
              </span>
              <span className="dashboard-user-info text-start d-none d-md-block">
                <span className="d-block fw-semibold text-dark small lh-sm">{displayName}</span>
                <span className="d-block text-muted" style={{ fontSize: '0.75rem' }}>
                  {roleLabel}
                </span>
              </span>
            </button>

            <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="userProfileDropdown">
              <li>
                <span className="dropdown-item-text px-3 py-2">
                  <small className="text-muted d-block">Signed in as</small>
                  <strong className="small">{displayName}</strong>
                  <small className="text-muted d-block mt-1">{roleLabel}</small>
                </span>
              </li>
              <li><hr className="dropdown-divider my-1" /></li>
              <li>
                <button type="button" className="dropdown-item" onClick={handleProfile}>
                  <i className="bi bi-person me-2" aria-hidden="true" />
                  Profile
                </button>
              </li>
              <li><hr className="dropdown-divider my-1" /></li>
              <li>
                <button
                  type="button"
                  className="dropdown-item text-danger"
                  onClick={handleLogout}
                >
                  <i className="bi bi-box-arrow-right me-2" aria-hidden="true" />
                  Logout
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </header>
  );
};

export default TopNavbar;
