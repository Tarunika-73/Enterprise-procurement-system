import { useEffect } from 'react';

const Toast = ({ show, message, type = 'success', onClose, duration = 4000 }) => {
  useEffect(() => {
    if (!show) return undefined;

    const timer = setTimeout(() => {
      onClose?.();
    }, duration);

    return () => clearTimeout(timer);
  }, [show, duration, onClose]);

  if (!show || !message) return null;

  const iconMap = {
    success: 'bi-check-circle-fill',
    danger: 'bi-x-circle-fill',
    warning: 'bi-exclamation-triangle-fill',
    info: 'bi-info-circle-fill',
  };

  return (
    <div
      className="toast-container position-fixed top-0 end-0 p-3 auth-toast-container"
      style={{ zIndex: 1080 }}
    >
      <div
        className={`toast show align-items-center text-bg-${type} border-0 shadow`}
        role="alert"
        aria-live="assertive"
        aria-atomic="true"
      >
        <div className="d-flex">
          <div className="toast-body d-flex align-items-center gap-2">
            <i className={`bi ${iconMap[type] || iconMap.info}`} aria-hidden="true" />
            {message}
          </div>
          <button
            type="button"
            className="btn-close btn-close-white me-2 m-auto"
            aria-label="Close notification"
            onClick={onClose}
          />
        </div>
      </div>
    </div>
  );
};

export default Toast;
