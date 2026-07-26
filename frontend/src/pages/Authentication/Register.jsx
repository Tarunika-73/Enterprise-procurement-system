import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout, { AuthDivider, AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import InputField from '../../components/Authentication/InputField/InputField';
import PasswordField from '../../components/Authentication/PasswordField/PasswordField';
import RoleDropdown from '../../components/Authentication/RoleDropdown/RoleDropdown';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import { register as registerApi, checkEmailExists } from '../../services/authService';
import {
  validateEmail,
  validateFullName,
  validateRegistrationPassword,
  validateConfirmPassword,
} from '../../utils/validation';

const Register = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: '',
  });
  const [errors, setErrors] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: '',
  });
  const [touched, setTouched] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [emailChecking, setEmailChecking] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const validateForm = useCallback(() => {
    const fullNameError = validateFullName(form.fullName);
    const emailError = validateEmail(form.email);
    const passwordError = validateRegistrationPassword(form.password);
    const confirmPasswordError = validateConfirmPassword(form.password, form.confirmPassword);
    const roleError = !form.role ? 'Please select a role.' : '';

    return {
      fullName: fullNameError,
      email: emailError,
      password: passwordError,
      confirmPassword: confirmPasswordError,
      role: roleError,
    };
  }, [form]);

  useEffect(() => {
    if (Object.keys(touched).length > 0) {
      setErrors(validateForm());
    }
  }, [form, touched, validateForm]);

  const isFormValid = Object.values(validateForm()).every((err) => !err);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleBlur = async (e) => {
    const { name } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));

    const validationErrors = validateForm();
    setErrors(validationErrors);

    if (name === 'email' && !validationErrors.email) {
      setEmailChecking(true);
      try {
        const exists = await checkEmailExists(form.email.trim());
        if (exists) {
          setErrors((prev) => ({ ...prev, email: 'Email already registered.' }));
        }
      } catch {
        // Backend not connected — skip duplicate check until API is integrated
      } finally {
        setEmailChecking(false);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched({
      fullName: true,
      email: true,
      password: true,
      confirmPassword: true,
      role: true,
    });

    const validationErrors = validateForm();
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) return;

    setIsLoading(true);
    try {
      const exists = await checkEmailExists(form.email.trim());
      if (exists) {
        setErrors((prev) => ({ ...prev, email: 'Email already registered.' }));
        return;
      }
    } catch {
      // Proceed when API is unavailable — backend will enforce on register
    }

    try {
      await registerApi({
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        password: form.password,
        role: form.role,
      });

      setToast({
        show: true,
        message: 'Registration successful! Please sign in with your credentials.',
        type: 'success',
      });

      setTimeout(() => navigate('/', { replace: true }), 2000);
    } catch {
      setToast({
        show: true,
        message: 'Registration failed. Please try again later.',
        type: 'danger',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout variant="internal">
      <AuthCard
        title="Create Account"
        subtitle="Register to access the Enterprise Procurement System"
      >
        <form onSubmit={handleSubmit} noValidate>
          <InputField
            id="fullName"
            name="fullName"
            label="Full Name"
            value={form.fullName}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="John Doe"
            error={touched.fullName ? errors.fullName : ''}
            required
            autoComplete="name"
          />

          <InputField
            id="register-email"
            name="email"
            label="Email Address"
            type="email"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="name@company.com"
            error={touched.email ? errors.email : ''}
            required
            disabled={emailChecking}
            autoComplete="email"
          />

          <PasswordField
            id="register-password"
            name="password"
            label="Password"
            value={form.password}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Create a strong password"
            error={touched.password ? errors.password : ''}
            required
            autoComplete="new-password"
          />

          <PasswordField
            id="confirmPassword"
            name="confirmPassword"
            label="Confirm Password"
            value={form.confirmPassword}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Re-enter your password"
            error={touched.confirmPassword ? errors.confirmPassword : ''}
            required
            autoComplete="new-password"
          />

          <RoleDropdown
            value={form.role}
            onChange={handleChange}
            onBlur={handleBlur}
            error={touched.role ? errors.role : ''}
          />

          <Button
            type="submit"
            isLoading={isLoading || emailChecking}
            disabled={!isFormValid || emailChecking}
            className="mt-2"
          >
            Register
          </Button>
        </form>

        <AuthDivider text="already registered" />

        <p className="text-center text-muted mb-0">
          Already have an account?{' '}
          <AuthLink to="/">Login</AuthLink>
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

export default Register;
