const DashboardStatCard = ({ icon, iconVariant = 'primary', value, label }) => {
  return (
    <div className="dashboard-stat-card">
      <div className={`dashboard-stat-icon ${iconVariant}`}>
        <i className={`bi ${icon}`} aria-hidden="true" />
      </div>
      <div className="dashboard-stat-value">{value}</div>
      <div className="dashboard-stat-label">{label}</div>
    </div>
  );
};

export default DashboardStatCard;
