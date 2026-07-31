import { useEffect, useState } from 'react';
import useVendorProfile from '../../hooks/useVendorProfile';
import InputField from '../../components/Authentication/InputField/InputField';
import Button from '../../components/Authentication/Button/Button';
import Toast from '../../components/Authentication/Toast/Toast';
import Loader from '../../components/Authentication/Loader/Loader';
import { getApiErrorMessage } from '../../utils/apiErrors';

const emptyForm = {
  vendorName: '',
  contactName: '',
  email: '',
  phone: '',
  address: '',
  gstNumber: '',
};

const VendorProfile = () => {
  const { vendor, isLoading, error, saveVendor } = useVendorProfile();
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [formErrors, setFormErrors] = useState({});
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  useEffect(() => {
    if (vendor) {
      setForm({
        vendorName: vendor.vendorName || '',
        contactName: vendor.contactName || '',
        email: vendor.email || '',
        phone: vendor.phone || '',
        address: vendor.address || '',
        gstNumber: vendor.gstNumber || '',
      });
    }
  }, [vendor]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setFormErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const validate = () => {
    const errors = {};
    if (!form.vendorName.trim()) errors.vendorName = 'Vendor name is required.';
    if (!form.email.trim()) errors.email = 'Email is required.';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = 'Enter a valid email address.';
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleCancel = () => {
    if (vendor) {
      setForm({
        vendorName: vendor.vendorName || '',
        contactName: vendor.contactName || '',
        email: vendor.email || '',
        phone: vendor.phone || '',
        address: vendor.address || '',
        gstNumber: vendor.gstNumber || '',
      });
    }
    setFormErrors({});
    setIsEditing(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setIsSaving(true);
    try {
      await saveVendor(form);
      setToast({ show: true, message: 'Vendor profile updated successfully.', type: 'success' });
      setIsEditing(false);
    } catch (err) {
      setToast({ show: true, message: getApiErrorMessage(err, 'Unable to update your profile.'), type: 'danger' });
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="d-flex align-items-center gap-2 text-muted">
        <Loader size="sm" />
        <span>Loading your vendor profile…</span>
      </div>
    );
  }

  if (error || !vendor) {
    return (
      <div className="alert alert-warning d-flex align-items-center gap-2" role="alert">
        <i className="bi bi-exclamation-triangle-fill" aria-hidden="true" />
        <span>{error || 'No vendor profile found for this account.'}</span>
      </div>
    );
  }

  return (
    <>
      <Toast
        show={toast.show}
        message={toast.message}
        type={toast.type}
        onClose={() => setToast((prev) => ({ ...prev, show: false }))}
      />

      <div className="dashboard-page-header d-flex flex-wrap align-items-start justify-content-between gap-2">
        <div>
          <h1>Vendor Profile</h1>
          <p className="text-muted mb-0">View and update your company details on file with procurement.</p>
        </div>
        <span className={`dashboard-vendor-status-tag ${vendor.isActive ? 'active' : 'inactive'}`}>
          {vendor.isActive ? 'Active' : 'Inactive'}
        </span>
      </div>

      <div className="dashboard-panel">
        <form onSubmit={handleSubmit} noValidate>
          <div className="row">
            <div className="col-md-6">
              <InputField
                label="Vendor / Company Name"
                name="vendorName"
                value={form.vendorName}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.vendorName}
                required
              />
            </div>
            <div className="col-md-6">
              <InputField
                label="Contact Person"
                name="contactName"
                value={form.contactName}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.contactName}
              />
            </div>
            <div className="col-md-6">
              <InputField
                label="Email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.email}
                required
              />
            </div>
            <div className="col-md-6">
              <InputField
                label="Phone"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.phone}
              />
            </div>
            <div className="col-md-6">
              <InputField
                label="GST Number"
                name="gstNumber"
                value={form.gstNumber}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.gstNumber}
              />
            </div>
            <div className="col-md-6">
              <InputField
                label="Address"
                name="address"
                value={form.address}
                onChange={handleChange}
                disabled={!isEditing}
                error={formErrors.address}
              />
            </div>
          </div>

          <div className="d-flex gap-2 justify-content-end mt-3">
            {!isEditing ? (
              <Button type="button" fullWidth={false} onClick={() => setIsEditing(true)}>
                <i className="bi bi-pencil-square me-2" aria-hidden="true" />
                Edit Profile
              </Button>
            ) : (
              <>
                <Button type="button" variant="outline-secondary" fullWidth={false} onClick={handleCancel} disabled={isSaving}>
                  Cancel
                </Button>
                <Button type="submit" fullWidth={false} isLoading={isSaving}>
                  Save Changes
                </Button>
              </>
            )}
          </div>
        </form>
      </div>
    </>
  );
};

export default VendorProfile;
