import { useEffect } from 'react';

/**
 * Enterprise Modal Container.
 * Accessible dialog container supporting custom sizes, header badges, action footers, and ESC key listener.
 */
const FinanceModal = ({
  isOpen = false,
  onClose,
  title = 'Modal',
  size = 'md',
  children,
  footer = null,
}) => {
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose?.();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const sizeClass = {
    sm: 'modal-sm',
    md: '',
    lg: 'modal-lg',
    xl: 'modal-xl',
  }[size] || '';

  return (
    <>
      {/* Modal Backdrop */}
      <div
        className="modal-backdrop fade show"
        style={{ zIndex: 1050 }}
        onClick={onClose}
      />

      {/* Modal Dialog */}
      <div
        className="modal fade show d-block"
        tabIndex="-1"
        role="dialog"
        aria-modal="true"
        style={{ zIndex: 1055 }}
      >
        <div className={`modal-dialog modal-dialog-centered modal-dialog-scrollable ${sizeClass}`}>
          <div className="modal-content shadow border-0">
            <div className="modal-header border-bottom-0 pb-0">
              <h5 className="modal-title fw-bold text-dark">{title}</h5>
              <button
                type="button"
                className="btn-close"
                aria-label="Close"
                onClick={onClose}
              />
            </div>
            <div className="modal-body py-3">{children}</div>
            {footer && <div className="modal-footer border-top-0 pt-0">{footer}</div>}
          </div>
        </div>
      </div>
    </>
  );
};

export default FinanceModal;
