import { NavLink } from 'react-router-dom';
import Logo from '../../Authentication/Logo/Logo';
import { useAuth } from '../../../context/AuthContext';
import { getNavItemsForRole, resolveNavItemPath } from '../../../utils/navigationConfig';
import { formatRoleLabel } from '../../../utils/userDisplay';

const Sidebar = ({ isOpen, onClose }) => {
  const { userRole } = useAuth();
  const navItems = getNavItemsForRole(userRole);

  const handleNavClick = () => {
    if (window.innerWidth < 992) {
      onClose?.();
    }
  };

  return (
    <>
      <div
        className={`dashboard-sidebar-backdrop ${isOpen ? 'show' : ''}`}
        onClick={onClose}
        aria-hidden="true"
      />

      <aside
        className={`dashboard-sidebar ${isOpen ? 'open' : ''}`}
        aria-label="Main navigation"
      >
        <div className="dashboard-sidebar-header">
          <Logo size="sm" />
        </div>

        <nav className="dashboard-sidebar-nav">
          <ul className="list-unstyled mb-0">
            {navItems.map((item) => {
              const path = resolveNavItemPath(item, userRole);
              const isComingSoon = item.comingSoon;

              if (isComingSoon) {
                return (
                  <li key={item.id}>
                    <span
                      className="dashboard-nav-link coming-soon"
                      aria-disabled="true"
                      title="Coming soon"
                    >
                      <i className={`bi ${item.icon}`} aria-hidden="true" />
                      <span>{item.label}</span>
                      <span className="dashboard-nav-badge">Soon</span>
                    </span>
                  </li>
                );
              }

              return (
                <li key={item.id}>
                  <NavLink
                    to={path}
                    end={item.id === 'dashboard' || item.id === 'my-requests' || item.id === 'notifications'}
                    className={({ isActive }) =>
                      `dashboard-nav-link ${isActive ? 'active' : ''}`
                    }
                    onClick={handleNavClick}
                  >
                    <i className={`bi ${item.icon}`} aria-hidden="true" />
                    <span>{item.label}</span>
                  </NavLink>
                </li>
              );
            })}
          </ul>
        </nav>

        <div className="dashboard-sidebar-footer">
          <div className="dashboard-sidebar-role">
            Signed in as{' '}
            <strong>{formatRoleLabel(userRole) || 'Guest'}</strong>
          </div>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
