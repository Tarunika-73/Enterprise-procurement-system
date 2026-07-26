import Loader from '../Loader/Loader';

const Button = ({
  type = 'button',
  variant = 'primary',
  size = 'lg',
  children,
  isLoading = false,
  disabled = false,
  className = '',
  onClick,
  fullWidth = true,
}) => {
  const isDisabled = disabled || isLoading;

  return (
    <button
      type={type}
      className={`btn btn-${variant} btn-${size} ${fullWidth ? 'w-100' : ''} auth-btn ${className}`}
      disabled={isDisabled}
      onClick={onClick}
      aria-busy={isLoading}
    >
      {isLoading ? (
        <span className="d-inline-flex align-items-center justify-content-center gap-2">
          <Loader size="sm" variant="light" />
          <span>Processing...</span>
        </span>
      ) : (
        children
      )}
    </button>
  );
};

export default Button;
