import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout, { AuthDivider, AuthLink } from '../../components/layouts/AuthLayout';
import AuthCard from '../../components/Authentication/AuthCard/AuthCard';
import InputField from '../../components/Authentication/InputField/InputField';
import PasswordField from '../../components/Authentication/PasswordField/PasswordField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import { vendorRegister } from '../../services/authService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import {
  validateEmail,
  validateRegistrationPassword,
  validateConfirmPassword,
} from '../../utils/validation';

const validatePhone = (phone) => {
  if (!phone?.trim()) return 'Phone number is required.';
  if (!/^\d{10}$/.test(phone.trim())) return 'Phone number must be exactly 10 digits.';
  return '';
};

const validateGst = (gst) => {
  if (!gst?.trim()) return 'GST number is required.';
  return '';
};

const VendorRegister = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    vendorName: '',
    contactName: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    gstNumber: '',
    address: '',
  });

  const [errors, setErrors] = useState({
    vendorName: '',
    contactName: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    gstNumber: '',
    address: '',
  });

  const [touched, setTouched] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const validateForm = useCallback(() => {
    return {
      vendorName: form.vendorName.trim() ? '' : 'Vendor company name is required.',
      contactName: form.contactName.trim() ? '' : 'Contact person name is required.',
      email: validateEmail(form.email),
      password: validateRegistrationPassword(form.password),
      confirmPassword: validateConfirmPassword(form.password, form.confirmPassword),
      phone: validatePhone(form.phone),
      gstNumber: validateGst(form.gstNumber),
      address: form.address.trim() ? '' : 'Address is required.',
    };
  }, [form]);

  useEffect(() => {
    if (Object.keys(touched).length > 0) {
      setErrors(validateForm());
    }
  }, [form, touched, validateForm]);

  const isFormValid = Object.values(validateForm()).every((e) => !e);

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
    const allTouched = Object.fromEntries(Object.keys(form).map((k) => [k, true]));
    setTouched(allTouched);

    const validationErrors = validateForm();
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) return;

    setIsLoading(true);
    try {
      await vendorRegister({
        vendorName: form.vendorName.trim(),
        contactName: form.contactName.trim(),
        email: form.email.trim(),
        password: form.password,
        phone: form.phone.trim(),
        address: form.address.trim(),
        gstNumber: form.gstNumber.trim(),
      });

      setToast({ show: true, message: 'Vendor registered successfully! Redirecting to login...', type: 'success' });
      setTimeout(() => navigate('/vendor-login', { replace: true }), 2000);
    } catch (error) {
      const msg = getApiErrorMessage(error, 'Registration failed. Please try again.');
      setToast({ show: true, message: msg, type: 'danger' });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout variant="vendor" template="reference" copyVariant="register">
      <AuthCard
        title="Vendor Registration"
        subtitle="Create your vendor account to access the procurement portal"
      >
        <form onSubmit={handleSubmit} noValidate>
          <InputField
            id="vendorName"
            name="vendorName"
            label="Vendor Company Name"
            value={form.vendorName}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="ABC Technologies"
            error={touched.vendorName ? errors.vendorName : ''}
            required
            icon="bi-building"
          />

          <InputField
            id="contactName"
            name="contactName"
            label="Contact Person"
            value={form.contactName}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Rahul Kumar"
            error={touched.contactName ? errors.contactName : ''}
            required
            icon="bi-person-fill"
          />

          <InputField
            id="vendor-reg-email"
            name="email"
            label="Email Address"
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
            id="vendor-reg-password"
            name="password"
            label="Password"
            value={form.password}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Create a strong password"
            error={touched.password ? errors.password : ''}
            required
            autoComplete="new-password"
            icon="bi-lock-fill"
          />

          <PasswordField
            id="vendor-reg-confirm-password"
            name="confirmPassword"
            label="Confirm Password"
            value={form.confirmPassword}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Re-enter your password"
            error={touched.confirmPassword ? errors.confirmPassword : ''}
            required
            autoComplete="new-password"
            icon="bi-lock-fill"
          />

          <InputField
            id="phone"
            name="phone"
            label="Phone Number"
            type="tel"
            value={form.phone}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="9876543210"
            error={touched.phone ? errors.phone : ''}
            required
            icon="bi-telephone-fill"
          />

          <InputField
            id="gstNumber"
            name="gstNumber"
            label="GST Number"
            value={form.gstNumber}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="GST001"
            error={touched.gstNumber ? errors.gstNumber : ''}
            required
            icon="bi-receipt"
          />

          <InputField
            id="address"
            name="address"
            label="Address"
            value={form.address}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Chennai"
            error={touched.address ? errors.address : ''}
            required
            icon="bi-geo-alt-fill"
          />

          <Button
            type="submit"
            isLoading={isLoading}
            disabled={!isFormValid}
            className="mt-2"
          >
            Register as Vendor
          </Button>
        </form>

        <AuthDivider text="already registered" />

        <p className="text-center text-muted mb-0">
          Already have a vendor account?{' '}
          <AuthLink to="/vendor-login">Sign In</AuthLink>
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

export default VendorRegister;
