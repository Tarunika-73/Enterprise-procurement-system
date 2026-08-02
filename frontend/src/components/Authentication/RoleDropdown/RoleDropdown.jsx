import { REGISTRATION_ROLES } from '../../../utils/constants';
import ValidationMessage from '../ValidationMessage/ValidationMessage';

const RoleDropdown = ({
  id = 'role',
  name = 'role',
  label = 'Role',
  value,
  onChange,
  onBlur,
  error,
  required = true,
  disabled = false,
  className = '',
}) => {
  return (
    <div className={`auth-field-group ${className}`}>
      <label htmlFor={id} className="auth-field-label">
        {label}
        {required && <span className="auth-field-required">*</span>}
      </label>
      <div className="auth-input-wrap has-icon">
        <span className="auth-input-icon" aria-hidden="true">
          <i className="bi bi-person-badge-fill" />
        </span>
        <select
          id={id}
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          disabled={disabled}
          className={`auth-input auth-select${error ? ' is-invalid' : ''}`}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${id}-error` : undefined}
        >
          <option value="">Select your role</option>
          {REGISTRATION_ROLES.map((role) => (
            <option key={role} value={role}>
              {role}
            </option>
          ))}
        </select>
      </div>
      <ValidationMessage id={`${id}-error`} message={error} />
    </div>
  );
};

export default RoleDropdown;
