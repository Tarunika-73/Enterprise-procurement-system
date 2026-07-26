import { useAuth } from '../../context/AuthContext';
import { getDisplayName } from '../../utils/userDisplay';
import DashboardStatCard from './DashboardStatCard';

/**
 * Reusable dashboard page shell — welcome banner + placeholder stat cards.
 * Used by all role-specific dashboard pages until real modules are built.
 */
const DashboardPageContent = ({ roleLabel, description, statCards }) => {
  const { user } = useAuth();
  const displayName = getDisplayName(user);

  return (
    <>
      <div className="dashboard-welcome-banner">
        <div className="d-flex flex-wrap align-items-start justify-content-between gap-2">
          <div>
            <h2>Welcome back, {displayName}</h2>
            <p className="text-muted mb-0">{description}</p>
          </div>
          <span className="dashboard-placeholder-tag">Placeholder Module</span>
        </div>
      </div>

      <div className="dashboard-page-header">
        <h1>{roleLabel} Overview</h1>
        <p className="text-muted mb-0">
          Key metrics and quick actions will appear here once modules are connected.
        </p>
      </div>

      <div className="row g-4">
        {statCards.map((card) => (
          <div key={card.label} className="col-sm-6 col-xl-3">
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>
    </>
  );
};

export default DashboardPageContent;
