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
    <div className={`mb-3 ${className}`}>
      <label htmlFor={id} className="form-label fw-medium text-secondary">
        {label}
        {required && <span className="text-danger ms-1">*</span>}
      </label>
      <select
        id={id}
        name={name}
        value={value}
        onChange={onChange}
        onBlur={onBlur}
        disabled={disabled}
        className={`form-select form-select-lg auth-input ${error ? 'is-invalid' : ''}`}
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
      <ValidationMessage id={`${id}-error`} message={error} />
    </div>
  );
};

export default RoleDropdown;
