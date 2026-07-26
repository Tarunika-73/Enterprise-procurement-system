import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout, { AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import ValidationMessage from '../../components/Authentication/ValidationMessage/ValidationMessage';
import { verifyOTP as verifyOTPApi, forgotPassword as forgotPasswordApi } from '../../services/authService';
import { OTP_LENGTH, OTP_RESEND_SECONDS, STORAGE_KEYS } from '../../utils/constants';

const OTPVerification = () => {
  const navigate = useNavigate();
  const inputRefs = useRef([]);

  const [otp, setOtp] = useState(Array(OTP_LENGTH).fill(''));
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [countdown, setCountdown] = useState(OTP_RESEND_SECONDS);
  const [canResend, setCanResend] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const email = sessionStorage.getItem(STORAGE_KEYS.RESET_EMAIL) || '';

  useEffect(() => {
    inputRefs.current[0]?.focus();
  }, []);

  useEffect(() => {
    if (countdown <= 0) {
      setCanResend(true);
      return undefined;
    }

    const timer = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [countdown]);

  const otpValue = otp.join('');
  const isFormValid = otpValue.length === OTP_LENGTH && /^\d+$/.test(otpValue);

  const handleChange = (index, value) => {
    if (value && !/^\d$/.test(value)) return;

    const nextOtp = [...otp];
    nextOtp[index] = value;
    setOtp(nextOtp);
    setError('');

    if (value && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
    if (e.key === 'ArrowLeft' && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
    if (e.key === 'ArrowRight' && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handlePaste = (e) => {
    e.preventDefault();
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, OTP_LENGTH);
    if (!pasted) return;

    const nextOtp = Array(OTP_LENGTH).fill('');
    pasted.split('').forEach((char, i) => {
      nextOtp[i] = char;
    });
    setOtp(nextOtp);

    const focusIndex = Math.min(pasted.length, OTP_LENGTH - 1);
    inputRefs.current[focusIndex]?.focus();
  };

  const handleResend = async () => {
    if (!canResend || !email) return;

    setIsLoading(true);
    try {
      await forgotPasswordApi(email);
      setToast({ show: true, message: 'A new OTP has been sent to your email.', type: 'success' });
      setCountdown(OTP_RESEND_SECONDS);
      setCanResend(false);
      setOtp(Array(OTP_LENGTH).fill(''));
      inputRefs.current[0]?.focus();
    } catch {
      setToast({ show: true, message: 'Failed to resend OTP. Please try again.', type: 'danger' });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isFormValid) {
      setError('Please enter the complete 6-digit OTP.');
      return;
    }

    setIsLoading(true);
    try {
      await verifyOTPApi({ email, otp: otpValue });
      navigate('/reset-password', { replace: true });
    } catch {
      setError('Invalid or expired OTP. Please try again.');
      setOtp(Array(OTP_LENGTH).fill(''));
      inputRefs.current[0]?.focus();
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout variant="internal">
      <AuthCard
        title="Verify OTP"
        subtitle={
          email
            ? `Enter the 6-digit code sent to ${email}`
            : 'Enter the 6-digit verification code sent to your email'
        }
      >
        <form onSubmit={handleSubmit} noValidate>
          <div className="auth-otp-group mb-3" onPaste={handlePaste}>
            {otp.map((digit, index) => (
              <input
                key={index}
                ref={(el) => {
                  inputRefs.current[index] = el;
                }}
                type="text"
                inputMode="numeric"
                maxLength={1}
                value={digit}
                onChange={(e) => handleChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                className={`form-control auth-otp-input ${error ? 'is-invalid' : ''}`}
                aria-label={`OTP digit ${index + 1}`}
              />
            ))}
          </div>

          <ValidationMessage message={error} />

          <div className="text-center my-4">
            {canResend ? (
              <button
                type="button"
                className="btn btn-link auth-link p-0"
                onClick={handleResend}
                disabled={isLoading}
              >
                Resend OTP
              </button>
            ) : (
              <span className="text-muted">
                Resend OTP in{' '}
                <span className="auth-timer">
                  {String(Math.floor(countdown / 60)).padStart(2, '0')}:
                  {String(countdown % 60).padStart(2, '0')}
                </span>
              </span>
            )}
          </div>

          <Button type="submit" isLoading={isLoading} disabled={!isFormValid}>
            Verify OTP
          </Button>
        </form>

        <p className="text-center text-muted mt-4 mb-0">
          <AuthLink to="/forgot-password">
            <i className="bi bi-arrow-left me-1" aria-hidden="true" />
            Back
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

export default OTPVerification;
