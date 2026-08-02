import { useEffect, useState } from 'react';
import { getVendorProfile, updateVendorProfile } from '../../services/vendorService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { formatDate } from '../../utils/employeeHelpers';

const VendorProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ contactName: '', phone: '', address: '' });

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await getVendorProfile();
        const data = res?.data ?? res;
        if (mounted) {
          setProfile(data);
          setForm({
            contactName: data.contactName || '',
            phone: data.phone || '',
            address: data.address || '',
          });
        }
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Unable to load profile.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      const res = await updateVendorProfile(form);
      const data = res?.data ?? res;
      setProfile(data);
      setSuccess('Profile updated successfully.');
      setEditing(false);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to update profile.'));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="dashboard-page-header d-flex align-items-start justify-content-between flex-wrap gap-2">
        <div>
          <h1>Vendor Profile</h1>
          <p className="text-muted mb-0">View and manage your company information.</p>
        </div>
        {!editing && (
          <button
            type="button"
            className="btn btn-outline-primary"
            onClick={() => { setEditing(true); setSuccess(''); setError(''); }}
          >
            <i className="bi bi-pencil me-2" />Edit Profile
          </button>
        )}
      </div>

      {error ? <div className="alert alert-danger">{error}</div> : null}
      {success ? <div className="alert alert-success">{success}</div> : null}

      <div className="row g-4">
        {/* Company Info */}
        <div className="col-lg-6">
          <div className="employee-detail-card h-100">
            <h2 className="h6 fw-bold mb-3">Company Information</h2>
            <dl className="employee-detail-list">
              <div><dt>Vendor ID</dt><dd className="text-muted">{profile?.id ?? '—'}</dd></div>
              <div><dt>Company Name</dt><dd>{profile?.vendorName || '—'}</dd></div>
              <div><dt>GST Number</dt><dd className="text-muted">{profile?.gstNumber || '—'}</dd></div>
              <div><dt>Email</dt><dd className="text-muted">{profile?.email || '—'}</dd></div>
              <div>
                <dt>Status</dt>
                <dd>
                  <span className={`badge text-bg-${profile?.isActive ? 'success' : 'secondary'}`}>
                    {profile?.isActive ? 'Active' : 'Inactive'}
                  </span>
                </dd>
              </div>
              <div><dt>Member Since</dt><dd>{formatDate(profile?.createdAt)}</dd></div>
            </dl>
          </div>
        </div>

        {/* Editable / Read-only Contact */}
        <div className="col-lg-6">
          <div className="employee-detail-card h-100">
            <h2 className="h6 fw-bold mb-3">Contact Details</h2>
            {editing ? (
              <form onSubmit={handleSave} noValidate>
                <div className="mb-3">
                  <label className="form-label fw-semibold" htmlFor="contactName">Contact Person</label>
                  <input
                    id="contactName"
                    name="contactName"
                    type="text"
                    className="form-control"
                    value={form.contactName}
                    onChange={handleChange}
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label fw-semibold" htmlFor="phone">Phone</label>
                  <input
                    id="phone"
                    name="phone"
                    type="tel"
                    className="form-control"
                    value={form.phone}
                    onChange={handleChange}
                  />
                </div>
                <div className="mb-4">
                  <label className="form-label fw-semibold" htmlFor="address">Address</label>
                  <textarea
                    id="address"
                    name="address"
                    className="form-control"
                    rows={3}
                    value={form.address}
                    onChange={handleChange}
                  />
                </div>
                <div className="d-flex gap-2">
                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? <span className="spinner-border spinner-border-sm me-2" /> : null}
                    Save Changes
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => {
                      setEditing(false);
                      setForm({
                        contactName: profile?.contactName || '',
                        phone: profile?.phone || '',
                        address: profile?.address || '',
                      });
                    }}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            ) : (
              <dl className="employee-detail-list">
                <div><dt>Contact Person</dt><dd>{profile?.contactName || '—'}</dd></div>
                <div><dt>Phone</dt><dd>{profile?.phone || '—'}</dd></div>
                <div><dt>Address</dt><dd>{profile?.address || '—'}</dd></div>
              </dl>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default VendorProfilePage;
