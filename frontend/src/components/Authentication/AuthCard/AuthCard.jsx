import { motion } from 'framer-motion';

const AuthCard = ({ title, subtitle, children, footer, className = '' }) => (
  <motion.div
    className={`auth-card card border-0 ${className}`}
    initial={{ opacity: 0, y: 28 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
  >
    <div className="card-body p-4 p-md-5">
      {(title || subtitle) && (
        <div className="auth-card-header mb-4">
          {title    && <h1 className="h3 fw-bold mb-2">{title}</h1>}
          {subtitle && <p className="text-muted mb-0">{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
    {footer && (
      <div className="card-footer bg-transparent border-0 px-4 px-md-5 pb-4">
        {footer}
      </div>
    )}
  </motion.div>
);

export default AuthCard;
