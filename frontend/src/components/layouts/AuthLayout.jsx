import { Link } from 'react-router-dom';
import Logo from '../Authentication/Logo/Logo';
import { APP_NAME, APP_VERSION, COPYRIGHT } from '../../utils/constants';

const WORKFLOW_STEPS = [
  { icon: 'bi-file-earmark-text', label: 'Purchase Request' },
  { icon: 'bi-cart-check', label: 'Purchase Order' },
  { icon: 'bi-truck', label: 'Vendor Delivery' },
  { icon: 'bi-currency-dollar', label: 'Finance Approval' },
];

const AuthLayout = ({
  children,
  variant = 'internal',
  showFooter = true,
}) => {
  const isVendor = variant === 'vendor';

  return (
    <div className="auth-layout min-vh-100">
      <div className="container-fluid g-0 min-vh-100">
        <div className="row g-0 min-vh-100">
          {/* Branding Panel */}
          <div className="col-lg-6 d-none d-lg-flex auth-brand-panel">
            <div className="auth-brand-content w-100 d-flex flex-column justify-content-between p-5">
              <Logo size="lg" />

              <div className="auth-brand-center my-4">
                <h2 className="display-6 fw-bold text-dark mb-3">{APP_NAME}</h2>
                <p className="lead text-secondary mb-4 auth-brand-description">
                  {isVendor
                    ? 'Secure vendor portal for purchase orders, invoicing, and supply chain collaboration.'
                    : 'Streamline procurement workflows — from purchase requests to finance approval — in one unified platform.'}
                </p>

                <div className="auth-illustration mb-4" aria-hidden="true">
                  <div className="auth-illustration-ring">
                    <i className="bi bi-diagram-3-fill auth-illustration-icon" />
                  </div>
                </div>

                <div className="auth-workflow-icons d-flex flex-wrap gap-3">
                  {WORKFLOW_STEPS.map((step) => (
                    <div key={step.label} className="auth-workflow-item text-center">
                      <div className="auth-workflow-icon-wrap mx-auto mb-2">
                        <i className={`bi ${step.icon}`} />
                      </div>
                      <small className="text-secondary fw-medium">{step.label}</small>
                    </div>
                  ))}
                </div>
              </div>

              <div className="auth-brand-footer text-muted small">
                <div>Version {APP_VERSION}</div>
                <div>{COPYRIGHT}</div>
              </div>
            </div>
          </div>

          {/* Form Panel */}
          <div className="col-lg-6 d-flex flex-column min-vh-100 auth-form-panel">
            <div className="d-lg-none p-4 pb-0">
              <Logo size="sm" />
            </div>

            <div className="flex-grow-1 d-flex align-items-center justify-content-center p-4 p-md-5">
              <div className="auth-form-wrapper w-100">{children}</div>
            </div>

            {showFooter && (
              <footer className="auth-mobile-footer text-center text-muted small py-3 d-lg-none">
                <div>Version {APP_VERSION}</div>
                <div>{COPYRIGHT}</div>
              </footer>
            )}
          </div>
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
