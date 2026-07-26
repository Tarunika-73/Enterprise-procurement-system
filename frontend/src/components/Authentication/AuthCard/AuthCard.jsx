const AuthCard = ({ title, subtitle, children, footer, className = '' }) => {
  return (
    <div className={`auth-card card border-0 shadow-lg ${className}`}>
      <div className="card-body p-4 p-md-5">
        {(title || subtitle) && (
          <div className="auth-card-header mb-4">
            {title && <h1 className="h3 fw-bold text-dark mb-2">{title}</h1>}
            {subtitle && <p className="text-muted mb-0">{subtitle}</p>}
          </div>
        )}
        {children}
      </div>
      {footer && <div className="card-footer bg-transparent border-0 px-4 px-md-5 pb-4">{footer}</div>}
    </div>
  );
};

export default AuthCard;
