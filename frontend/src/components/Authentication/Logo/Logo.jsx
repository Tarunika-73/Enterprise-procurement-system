import { APP_NAME, APP_SHORT_NAME } from '../../../utils/constants';

const Logo = ({ size = 'md', showText = true, className = '' }) => {
  const iconSize = size === 'lg' ? 56 : size === 'sm' ? 36 : 48;

  return (
    <div className={`d-flex align-items-center gap-3 ${className}`}>
      <div
        className="auth-logo-icon d-flex align-items-center justify-content-center flex-shrink-0"
        style={{ width: iconSize, height: iconSize }}
        aria-hidden="true"
      >
        <i className="bi bi-box-seam-fill text-white" style={{ fontSize: iconSize * 0.45 }} />
      </div>
      {showText && (
        <div>
          <div className="auth-logo-short fw-bold">{APP_SHORT_NAME}</div>
          <div className="auth-logo-full text-muted small">{APP_NAME}</div>
        </div>
      )}
    </div>
  );
};

export default Logo;
