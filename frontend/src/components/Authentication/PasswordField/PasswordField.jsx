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
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const inputId = id || name;

  return (
    <div className={`mb-3 ${className}`}>
      {label && (
        <label htmlFor={inputId} className="form-label fw-medium text-secondary">
          {label}
          {required && <span className="text-danger ms-1">*</span>}
        </label>
      )}
      <div className="input-group input-group-lg">
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
          className={`form-control auth-input ${error ? 'is-invalid' : ''}`}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${inputId}-error` : undefined}
        />
        <button
          type="button"
          className="btn btn-outline-secondary auth-toggle-btn"
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
