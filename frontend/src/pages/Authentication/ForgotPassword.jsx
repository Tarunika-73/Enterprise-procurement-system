import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout, { AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import InputField from '../../components/Authentication/InputField/InputField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import { forgotPassword as forgotPasswordApi } from '../../services/authService';
import { validateEmail } from '../../utils/validation';
import { STORAGE_KEYS } from '../../utils/constants';

const ForgotPassword = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [touched, setTouched] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const emailError = validateEmail(email);
  const isFormValid = !emailError;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched(true);
    setError(emailError);
    if (emailError) return;

    setIsLoading(true);
    try {
      await forgotPasswordApi(email.trim());

      sessionStorage.setItem(STORAGE_KEYS.RESET_EMAIL, email.trim());

      setToast({
        show: true,
        message: 'OTP has been sent to your registered email address.',
        type: 'success',
      });

      setTimeout(() => navigate('/verify-otp'), 1500);
    } catch {
      setToast({
        show: true,
        message: 'Unable to send OTP. Please verify your email and try again.',
        type: 'danger',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout variant="internal">
      <AuthCard
        title="Forgot Password"
        subtitle="Enter your registered email to receive a one-time password"
      >
        <form onSubmit={handleSubmit} noValidate>
          <InputField
            id="forgot-email"
            name="email"
            label="Email Address"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onBlur={() => {
              setTouched(true);
              setError(validateEmail(email));
            }}
            placeholder="name@company.com"
            error={touched ? error || emailError : ''}
            required
            autoComplete="email"
          />

          <Button type="submit" isLoading={isLoading} disabled={!isFormValid} className="mb-3">
            Send OTP
          </Button>
        </form>

        <p className="text-center text-muted mb-0">
          <AuthLink to="/">
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

export default ForgotPassword;
