import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getDisplayName, formatRoleLabel } from '../../utils/userDisplay';
import FinanceModal from '../../components/finance/FinanceModal';

const ProfilePage = () => {
  const navigate = useNavigate();
  const { user, userRole, logout } = useAuth();

  const [profileData, setProfileData] = useState({
    name: getDisplayName(user) || 'David Miller',
    email: user?.email || 'david.miller@enterprise.com',
    role: formatRoleLabel(userRole) || 'Finance Manager',
    department: 'Finance & Compliance',
    phone: '+1 (555) 432-8901',
  });

  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [passwordError, setPasswordError] = useState('');
  const [toastMessage, setToastMessage] = useState(null);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleUpdateProfile = (e) => {
    e.preventDefault();
    showToast('Profile updated successfully!');
  };

  const handleChangePasswordSubmit = (e) => {
    e.preventDefault();
    if (!passwordForm.currentPassword) {
      setPasswordError('Current Password is required');
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      setPasswordError('New Password must be at least 6 characters');
      return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('New Passwords do not match');
      return;
    }

    setPasswordError('');
    setIsPasswordModalOpen(false);
    setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    showToast('Password changed successfully!');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="finance-profile-page container-fluid py-3">
      {/* Toast Alert */}
      {toastMessage && (
        <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-check-circle me-2" />
          {toastMessage}
        </div>
      )}

      {/* Header */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div>
          <h4 className="fw-bold text-dark mb-1">User Profile & Account</h4>
          <p className="text-muted small mb-0">
            Manage your credentials, personnel information, and security options.
          </p>
        </div>

        <button className="btn btn-sm btn-outline-danger" onClick={handleLogout}>
          <i className="bi bi-box-arrow-right me-1" /> Logout
        </button>
      </div>

      <div className="row g-4">
        {/* User Card */}
        <div className="col-12 col-md-4">
          <div className="card border-0 shadow-sm text-center p-4">
            <div
              className="mx-auto bg-primary text-white rounded-circle d-flex align-items-center justify-content-center fw-bold fs-2 mb-3"
              style={{ width: 80, height: 80 }}
            >
              {profileData.name
                .split(' ')
                .map((n) => n[0])
                .join('')}
            </div>
            <h5 className="fw-bold text-dark mb-1">{profileData.name}</h5>
            <span className="badge bg-light text-primary border mx-auto mb-3">
              {profileData.role}
            </span>
            <p className="small text-muted mb-3">{profileData.department}</p>
            <button
              className="btn btn-sm btn-outline-secondary w-100"
              onClick={() => setIsPasswordModalOpen(true)}
            >
              <i className="bi bi-key me-1" /> Change Password
            </button>
          </div>
        </div>

        {/* Edit Profile Form */}
        <div className="col-12 col-md-8">
          <div className="card border-0 shadow-sm p-4">
            <h6 className="fw-bold text-dark mb-3">Personal & Department Information</h6>
            <form onSubmit={handleUpdateProfile} className="row g-3">
              <div className="col-12 col-md-6">
                <label className="form-label fw-semibold small">Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={profileData.name}
                  onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                />
              </div>

              <div className="col-12 col-md-6">
                <label className="form-label fw-semibold small">Email Address</label>
                <input
                  type="email"
                  className="form-control"
                  value={profileData.email}
                  onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                />
              </div>

              <div className="col-12 col-md-6">
                <label className="form-label fw-semibold small">Role Title</label>
                <input
                  type="text"
                  className="form-control bg-light"
                  value={profileData.role}
                  disabled
                />
              </div>

              <div className="col-12 col-md-6">
                <label className="form-label fw-semibold small">Department</label>
                <input
                  type="text"
                  className="form-control"
                  value={profileData.department}
                  onChange={(e) => setProfileData({ ...profileData, department: e.target.value })}
                />
              </div>

              <div className="col-12 col-md-6">
                <label className="form-label fw-semibold small">Phone Number</label>
                <input
                  type="text"
                  className="form-control"
                  value={profileData.phone}
                  onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                />
              </div>

              <div className="col-12 pt-3">
                <button type="submit" className="btn btn-primary">
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Change Password Modal */}
      <FinanceModal
        isOpen={isPasswordModalOpen}
        onClose={() => setIsPasswordModalOpen(false)}
        title="Change Security Password"
        size="md"
        footer={
          <div className="d-flex justify-content-end gap-2 w-100">
            <button
              className="btn btn-secondary"
              onClick={() => setIsPasswordModalOpen(false)}
            >
              Cancel
            </button>
            <button className="btn btn-primary" onClick={handleChangePasswordSubmit}>
              Update Password
            </button>
          </div>
        }
      >
        <form onSubmit={handleChangePasswordSubmit} className="row g-3">
          {passwordError && (
            <div className="col-12">
              <div className="alert alert-danger p-2 small">{passwordError}</div>
            </div>
          )}

          <div className="col-12">
            <label className="form-label fw-semibold small">Current Password *</label>
            <input
              type="password"
              className="form-control"
              value={passwordForm.currentPassword}
              onChange={(e) =>
                setPasswordForm({ ...passwordForm, currentPassword: e.target.value })
              }
            />
          </div>

          <div className="col-12">
            <label className="form-label fw-semibold small">New Password *</label>
            <input
              type="password"
              className="form-control"
              value={passwordForm.newPassword}
              onChange={(e) =>
                setPasswordForm({ ...passwordForm, newPassword: e.target.value })
              }
            />
          </div>

          <div className="col-12">
            <label className="form-label fw-semibold small">Confirm New Password *</label>
            <input
              type="password"
              className="form-control"
              value={passwordForm.confirmPassword}
              onChange={(e) =>
                setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })
              }
            />
          </div>
        </form>
      </FinanceModal>
    </div>
  );
};

export default ProfilePage;
