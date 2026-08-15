import { useCallback, useEffect, useState } from 'react';
import AuthLayout, { AuthDivider, AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import InputField from '../../components/Authentication/InputField/InputField';
import PasswordField from '../../components/Authentication/PasswordField/PasswordField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import useLogin from '../../hooks/useLogin';
import { vendorLogin } from '../../services/authService';
import { validateEmail, validatePasswordRequired } from '../../utils/validation';

const VendorLogin = () => {
  const { submitLogin, isLoading, toast, dismissToast } = useLogin(
    'VENDOR',
    'Vendor login failed. Please verify your credentials or contact procurement support.',
    vendorLogin
  );

  const [form, setForm] = useState({ email: '', password: '', rememberMe: false });
  const [errors, setErrors] = useState({ email: '', password: '' });
  const [touched, setTouched] = useState({ email: false, password: false });

  const validateForm = useCallback(() => {
    const emailError = validateEmail(form.email);
    const passwordError = validatePasswordRequired(form.password);
    return { email: emailError, password: passwordError };
  }, [form.email, form.password]);

  useEffect(() => {
    if (touched.email || touched.password) {
      setErrors(validateForm());
    }
  }, [form.email, form.password, touched, validateForm]);

  const isFormValid = !validateForm().email && !validateForm().password;

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    setErrors(validateForm());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched({ email: true, password: true });

    const validationErrors = validateForm();
    setErrors(validationErrors);
    if (validationErrors.email || validationErrors.password) return;

    await submitLogin({
      email: form.email,
      password: form.password,
    });
  };

  return (
    <AuthLayout variant="vendor" template="reference">
      <AuthCard
        title="Vendor Portal"
        subtitle="Sign in to manage orders, invoices, and deliveries"
      >
        <form onSubmit={handleSubmit} noValidate>
          <InputField
            id="vendor-email"
            name="email"
            label="Email"
            type="email"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="vendor@company.com"
            error={touched.email ? errors.email : ''}
            required
            autoComplete="email"
            icon="bi-envelope-fill"
          />

          <PasswordField
            id="vendor-password"
            name="password"
            label="Password"
            value={form.password}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.password ? errors.password : ''}
            required
            icon="bi-lock-fill"
          />

          <div className="d-flex justify-content-between align-items-center mb-4">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                id="vendorRememberMe"
                name="rememberMe"
                checked={form.rememberMe}
                onChange={handleChange}
              />
              <label className="form-check-label text-secondary" htmlFor="vendorRememberMe">
                Remember me
              </label>
            </div>
            <AuthLink to="/forgot-password">Forgot Password?</AuthLink>
          </div>

          <Button type="submit" isLoading={isLoading} disabled={!isFormValid}>
            Vendor Sign In
          </Button>
        </form>

        <AuthDivider />

        <p className="text-center text-muted mb-2">
          Don't have a Vendor Account?{' '}
          <AuthLink to="/vendor/register">Register as Vendor</AuthLink>
        </p>

        <p className="text-center text-muted small mb-0">
          Internal employee?{' '}
          <AuthLink to="/login">Internal Login</AuthLink>
        </p>
      </AuthCard>

      <Toast
        show={toast.show}
        message={toast.message}
        type={toast.type}
        onClose={dismissToast}
      />
    </AuthLayout>
  );
};

export default VendorLogin;
