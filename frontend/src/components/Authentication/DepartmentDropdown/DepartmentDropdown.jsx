import ValidationMessage from '../ValidationMessage/ValidationMessage';

const DepartmentDropdown = ({
  id = 'departmentId',
  name = 'departmentId',
  label = 'Department',
  value,
  onChange,
  onBlur,
  error,
  required = true,
  disabled = false,
  departments = [],
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
          <i className="bi bi-building" />
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
          <option value="">Select your department</option>
          {departments.map((department) => (
            <option key={department.id} value={department.id}>
              {department.name}
            </option>
          ))}
        </select>
      </div>
      <ValidationMessage id={`${id}-error`} message={error} />
    </div>
  );
};

export default DepartmentDropdown;
