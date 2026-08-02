import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout, { AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import PasswordField from '../../components/Authentication/PasswordField/PasswordField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import { resetPassword as resetPasswordApi } from '../../services/authService';
import {
  validateRegistrationPassword,
  validateConfirmPassword,
  getPasswordStrength,
  PASSWORD_STRENGTH_LABELS,
  PASSWORD_REQUIREMENTS,
} from '../../utils/validation';
import { STORAGE_KEYS } from '../../utils/constants';

const ResetPassword = () => {
  const navigate = useNavigate();
  const email = sessionStorage.getItem(STORAGE_KEYS.RESET_EMAIL) || '';

  const [form, setForm] = useState({ password: '', confirmPassword: '' });
  const [errors, setErrors] = useState({ password: '', confirmPassword: '' });
  const [touched, setTouched] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const validateForm = useCallback(() => {
    const passwordError = validateRegistrationPassword(form.password);
    const confirmPasswordError = validateConfirmPassword(form.password, form.confirmPassword);
    return { password: passwordError, confirmPassword: confirmPasswordError };
  }, [form]);

  useEffect(() => {
    if (Object.keys(touched).length > 0) {
      setErrors(validateForm());
    }
  }, [form, touched, validateForm]);

  const strength = getPasswordStrength(form.password);
  const isFormValid = Object.values(validateForm()).every((err) => !err);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    setErrors(validateForm());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched({ password: true, confirmPassword: true });

    const validationErrors = validateForm();
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) return;

    setIsLoading(true);
    try {
      await resetPasswordApi({
        email,
        password: form.password,
        confirmPassword: form.confirmPassword,
      });

      sessionStorage.removeItem(STORAGE_KEYS.RESET_EMAIL);

      setToast({
        show: true,
        message: 'Password reset successfully! Redirecting to login...',
        type: 'success',
      });

      setTimeout(() => navigate('/login', { replace: true }), 2000);
    } catch {
      setToast({
        show: true,
        message: 'Password reset failed. Please try again.',
        type: 'danger',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout variant="internal">
      <AuthCard
        title="Reset Password"
        subtitle="Create a new secure password for your account"
      >
        <form onSubmit={handleSubmit} noValidate>
          <PasswordField
            id="new-password"
            name="password"
            label="New Password"
            value={form.password}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Enter new password"
            error={touched.password ? errors.password : ''}
            required
            autoComplete="new-password"
          />

          {form.password && (
            <div className="mb-3">
              <div className="d-flex justify-content-between align-items-center">
                <small className="text-muted">Password Strength</small>
                <small className={`fw-semibold text-${strength >= 3 ? 'success' : strength >= 2 ? 'warning' : 'danger'}`}>
                  {PASSWORD_STRENGTH_LABELS[strength]}
                </small>
              </div>
              <div className="auth-strength-meter">
                <div className={`auth-strength-bar auth-strength-${strength}`} />
              </div>
            </div>
          )}

          <div className="auth-requirements-card mb-4">
            <p className="small fw-semibold text-secondary mb-2">Password Requirements</p>
            {PASSWORD_REQUIREMENTS.map((req) => {
              const met = req.test(form.password);
              return (
                <div key={req.id} className={`auth-requirement-item ${met ? 'met' : ''}`}>
                  <i className={`bi ${met ? 'bi-check-circle-fill' : 'bi-circle'}`} aria-hidden="true" />
                  {req.label}
                </div>
              );
            })}
          </div>

          <PasswordField
            id="reset-confirm-password"
            name="confirmPassword"
            label="Confirm Password"
            value={form.confirmPassword}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Re-enter new password"
            error={touched.confirmPassword ? errors.confirmPassword : ''}
            required
            autoComplete="new-password"
          />

          <Button type="submit" isLoading={isLoading} disabled={!isFormValid}>
            Reset Password
          </Button>
        </form>

        <p className="text-center text-muted mt-4 mb-0">
          <AuthLink to="/login">
            <i className="bi bi-arrow-left me-1" aria-hidden="true" />
            Back to Login
          </AuthLink>
        </p>
      </AuthCard>

      <Toast
        show={toast.show}
        message={toast.message}
        type={toast.type}
        onClose={() => setToast((prev) => ({ ...prev, show: false }))}
      />
    </AuthLayout>
  );
};

export default ResetPassword;
