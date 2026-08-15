import { useCallback, useEffect, useState } from 'react';
import AuthLayout, { AuthDivider, AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import InputField from '../../components/Authentication/InputField/InputField';
import PasswordField from '../../components/Authentication/PasswordField/PasswordField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import useLogin from '../../hooks/useLogin';
import { validateEmail, validatePasswordRequired } from '../../utils/validation';

const InternalLogin = () => {
  const { submitLogin, isLoading, toast, dismissToast } = useLogin(
    'INTERNAL',
    'Login failed. Please verify your credentials or try again later.'
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
    <AuthLayout variant="internal" template="reference">
      <AuthCard
        title="Welcome Back"
        subtitle="Sign in to access your procurement workspace"
      >
        <form onSubmit={handleSubmit} noValidate>
          <InputField
            id="email"
            name="email"
            label="Email"
            type="email"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="name@company.com"
            error={touched.email ? errors.email : ''}
            required
            autoComplete="email"
            icon="bi-envelope-fill"
          />

          <PasswordField
            id="password"
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
                id="rememberMe"
                name="rememberMe"
                checked={form.rememberMe}
                onChange={handleChange}
              />
              <label className="form-check-label text-secondary" htmlFor="rememberMe">
                Remember me
              </label>
            </div>
            <AuthLink to="/forgot-password">Forgot Password?</AuthLink>
          </div>

          <Button type="submit" isLoading={isLoading} disabled={!isFormValid}>
            Sign In
          </Button>
        </form>

        <AuthDivider />

        <p className="text-center text-muted mb-2">
          Don&apos;t have an account?{' '}
          <AuthLink to="/register">Sign Up</AuthLink>
        </p>

        <p className="text-center text-muted small mb-0">
          Are you a vendor?{' '}
          <AuthLink to="/vendor-login">Vendor Login</AuthLink>
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

export default InternalLogin;
