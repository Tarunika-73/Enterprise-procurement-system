import { motion } from 'framer-motion';

const AuthCard = ({ title, subtitle, children, footer, className = '' }) => (
  <motion.div
    className={`auth-card-content ${className}`}
    initial={{ opacity: 0, y: 18 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
  >
    {(title || subtitle) && (
      <div className="auth-card-header">

  <div className="auth-login-logo">
    <i className="bi bi-building-fill-check"></i>
  </div>

  {title && <h1 className="auth-card-title">{title}</h1>}
        {subtitle && <p className="auth-card-subtitle">{subtitle}</p>}
        <div className="auth-card-divider" aria-hidden="true" />
      </div>
    )}
    {children}
    {footer && <div className="auth-card-footer">{footer}</div>}
  </motion.div>
);

export default AuthCard;
