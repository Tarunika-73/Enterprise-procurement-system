import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Logo from '../Authentication/Logo/Logo';
import { APP_NAME, APP_VERSION, COPYRIGHT } from '../../utils/constants';

const WORKFLOW_STEPS = [
  { icon: 'bi-file-earmark-text', label: 'Purchase Request' },
  { icon: 'bi-cart-check',        label: 'Purchase Order'   },
  { icon: 'bi-truck',             label: 'Vendor Delivery'  },
  { icon: 'bi-currency-dollar',   label: 'Finance Approval' },
];

const panelVariants = {
  hidden:  { opacity: 0, x: -32 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.7, ease: [0.22, 1, 0.36, 1] } },
};

const formVariants = {
  hidden:  { opacity: 0, x: 32 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.7, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = {
  hidden:  {},
  visible: { transition: { staggerChildren: 0.10, delayChildren: 0.2 } },
};

const fadeUp = {
  hidden:  { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.55, ease: [0.22, 1, 0.36, 1] } },
};

const AuthLayout = ({ children, variant = 'internal', showFooter = true }) => {
  const isVendor = variant === 'vendor';

  return (
    <div className="auth-layout min-vh-100">
      <div className="container-fluid g-0 min-vh-100">
        <div className="row g-0 min-vh-100">

          {/* ── Brand panel ── */}
          <motion.div
            className="col-lg-6 d-none d-lg-flex auth-brand-panel"
            initial="hidden"
            animate="visible"
            variants={panelVariants}
          >
            <motion.div
              className="auth-brand-content w-100 d-flex flex-column justify-content-between p-5"
              initial="hidden"
              animate="visible"
              variants={stagger}
            >
              <motion.div variants={fadeUp}>
                <Logo size="lg" />
              </motion.div>

              <div className="auth-brand-center my-4">
                <motion.h2
                  className="display-6 fw-bold mb-3"
                  style={{ color: '#0f0a1e', letterSpacing: '-0.5px' }}
                  variants={fadeUp}
                >
                  {APP_NAME}
                </motion.h2>

                <motion.p className="auth-brand-description mb-4" variants={fadeUp}>
                  {isVendor
                    ? 'Secure vendor portal for purchase orders, invoicing, and supply chain collaboration.'
                    : 'Streamline procurement workflows — from purchase requests to finance approval — in one unified platform.'}
                </motion.p>

                <motion.div className="auth-illustration mb-4" aria-hidden="true" variants={fadeUp}>
                  <div className="auth-illustration-ring">
                    <i className="bi bi-diagram-3-fill auth-illustration-icon" />
                  </div>
                </motion.div>

                <motion.div className="auth-workflow-icons d-flex flex-wrap gap-3" variants={stagger}>
                  {WORKFLOW_STEPS.map((step) => (
                    <motion.div
                      key={step.label}
                      className="auth-workflow-item text-center"
                      variants={fadeUp}
                    >
                      <div className="auth-workflow-icon-wrap mx-auto mb-2">
                        <i className={`bi ${step.icon}`} />
                      </div>
                      <small className="fw-semibold" style={{ color: '#6b7280', fontSize: '0.75rem' }}>
                        {step.label}
                      </small>
                    </motion.div>
                  ))}
                </motion.div>
              </div>

              <motion.div
                className="auth-brand-footer small"
                style={{ color: '#9ca3af' }}
                variants={fadeUp}
              >
                <div>Version {APP_VERSION}</div>
                <div>{COPYRIGHT}</div>
              </motion.div>
            </motion.div>
          </motion.div>

          {/* ── Form panel ── */}
          <motion.div
            className="col-lg-6 d-flex flex-column min-vh-100 auth-form-panel"
            initial="hidden"
            animate="visible"
            variants={formVariants}
          >
            <div className="d-lg-none p-4 pb-0">
              <Logo size="sm" />
            </div>

            <div className="flex-grow-1 d-flex align-items-center justify-content-center p-4 p-md-5">
              <div className="auth-form-wrapper w-100">{children}</div>
            </div>

            {showFooter && (
              <footer className="auth-mobile-footer text-center py-3 d-lg-none">
                <div>Version {APP_VERSION}</div>
                <div>{COPYRIGHT}</div>
              </footer>
            )}
          </motion.div>

        </div>
      </div>
    </div>
  );
};

export const AuthDivider = ({ text = 'or' }) => (
  <div className="auth-divider d-flex align-items-center my-4">
    <hr className="flex-grow-1" />
    <span className="px-3 text-muted small text-uppercase">{text}</span>
    <hr className="flex-grow-1" />
  </div>
);

export const AuthLink = ({ to, children, className = '' }) => (
  <Link to={to} className={`auth-link fw-semibold text-decoration-none ${className}`}>
    {children}
  </Link>
);

export default AuthLayout;
