import { useState } from 'react';
import ValidationMessage from '../ValidationMessage/ValidationMessage';

const PasswordField = ({
  id,
  label,
  name,
  value,
  onChange,
  onBlur,
  placeholder = 'Enter your password',
  error,
  required = false,
  disabled = false,
  autoComplete = 'current-password',
  className = '',
  icon = 'bi-lock-fill',
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const inputId = id || name;

  return (
    <div className={`auth-field-group ${className}`}>
      {label && (
        <label htmlFor={inputId} className="auth-field-label">
          {label}
          {required && <span className="auth-field-required">*</span>}
        </label>
      )}
      <div className="auth-input-wrap has-icon has-suffix">
        <span className="auth-input-icon" aria-hidden="true">
          <i className={`bi ${icon}`} />
        </span>
        <input
          id={inputId}
          type={showPassword ? 'text' : 'password'}
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
        <button
          type="button"
          className="auth-toggle-btn"
          onClick={() => setShowPassword((prev) => !prev)}
          aria-label={showPassword ? 'Hide password' : 'Show password'}
          tabIndex={-1}
        >
          <i className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
        </button>
      </div>
      <ValidationMessage id={`${inputId}-error`} message={error} />
    </div>
  );
};

export default PasswordField;
