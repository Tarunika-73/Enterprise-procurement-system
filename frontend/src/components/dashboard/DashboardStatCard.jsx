const DashboardStatCard = ({ icon, iconVariant = 'primary', value, label, onClick, active = false }) => {
  return (
    <div className={`dashboard-stat-card ${onClick ? 'dashboard-stat-card-clickable' : ''} ${active ? 'active' : ''}`} role={onClick ? 'button' : undefined} tabIndex={onClick ? 0 : undefined} onClick={onClick} onKeyDown={(event) => { if (onClick && (event.key === 'Enter' || event.key === ' ')) { event.preventDefault(); onClick(); } }}>
      <div className={`dashboard-stat-icon ${iconVariant}`}>
        <i className={`bi ${icon}`} aria-hidden="true" />
      </div>
      <div className="dashboard-stat-content"><div className="dashboard-stat-value">{value}</div><div className="dashboard-stat-label">{label}</div></div>
    </div>
  );
};

export default DashboardStatCard;
