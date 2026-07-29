import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import { getDisplayName, getUserInitials, formatRoleLabel } from '../../../utils/userDisplay';
import { STORAGE_KEYS } from '../../../utils/constants';

const TopNavbar = ({ pageTitle, onToggleSidebar }) => {
  const navigate = useNavigate();
  const { user, userRole, logout } = useAuth();

  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem(STORAGE_KEYS.THEME) === 'dark';
  });

  useEffect(() => {
    if (darkMode) {
      document.documentElement.setAttribute('data-theme', 'dark');
      localStorage.setItem(STORAGE_KEYS.THEME, 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
      localStorage.setItem(STORAGE_KEYS.THEME, 'light');
    }
  }, [darkMode]);

  const displayName = getDisplayName(user);
  const initials = getUserInitials(user);
  const roleLabel = formatRoleLabel(userRole);

  const handleLogout = () => {
    logout();
    navigate('/');
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
          {/* Dark Mode Toggle */}
          <button
            type="button"
            className="dashboard-navbar-btn"
            onClick={() => setDarkMode((prev) => !prev)}
            aria-label="Toggle Theme Mode"
            title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          >
            <i className={`bi ${darkMode ? 'bi-sun-fill text-warning' : 'bi-moon-stars'}`} />
          </button>

          {/* Notifications button */}
          <button
            type="button"
            className="dashboard-navbar-btn position-relative"
            aria-label="Notifications"
            title="Notifications"
            onClick={() => navigate('/dashboard/finance/notifications')}
          >
            <i className="bi bi-bell" />
            <span className="dashboard-notification-badge" aria-hidden="true" />
          </button>

          {/* User Profile Dropdown */}
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
                <button
                  type="button"
                  className="dropdown-item"
                  onClick={() => navigate('/dashboard/finance/profile')}
                >
                  <i className="bi bi-person me-2" aria-hidden="true" />
                  Profile
                </button>
              </li>
              <li>
                <button
                  type="button"
                  className="dropdown-item"
                  onClick={() => navigate('/dashboard/finance/profile')}
                >
                  <i className="bi bi-gear me-2" aria-hidden="true" />
                  Settings
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
