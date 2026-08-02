import ValidationMessage from '../ValidationMessage/ValidationMessage';

const InputField = ({
  id,
  label,
  type = 'text',
  name,
  value,
  onChange,
  onBlur,
  placeholder,
  error,
  required = false,
  disabled = false,
  autoComplete,
  className = '',
  icon,
}) => {
  const inputId = id || name;

  return (
    <div className={`auth-field-group ${className}`}>
      {label && (
        <label htmlFor={inputId} className="auth-field-label">
          {label}
          {required && <span className="auth-field-required">*</span>}
        </label>
      )}
      <div className={`auth-input-wrap${icon ? ' has-icon' : ''}`}>
        {icon && (
          <span className="auth-input-icon" aria-hidden="true">
            <i className={`bi ${icon}`} />
          </span>
        )}
        <input
          id={inputId}
          type={type}
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          placeholder={placeholder}
          disabled={disabled}
          autoComplete={autoComplete}
          className={`auth-input${error ? ' is-invalid' : ''}`}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${inputId}-error` : undefined}
        />
      </div>
      <ValidationMessage id={`${inputId}-error`} message={error} />
    </div>
  );
};

export default InputField;
