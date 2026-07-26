const Loader = ({ size = 'md', variant = 'primary', className = '' }) => {
  const sizeClass = size === 'sm' ? 'spinner-border-sm' : '';

  return (
    <span
      className={`spinner-border text-${variant} ${sizeClass} ${className}`}
      role="status"
      aria-label="Loading"
    >
      <span className="visually-hidden">Loading...</span>
    </span>
  );
};

export default Loader;
