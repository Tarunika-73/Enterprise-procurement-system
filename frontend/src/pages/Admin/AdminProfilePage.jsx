import { useAuth } from '../../context/AuthContext';
import { formatRoleLabel, getDisplayName } from '../../utils/userDisplay';

const AdminProfilePage = () => {
  const { user } = useAuth();
  const fields = [
    ['Name', getDisplayName(user)],
    ['Employee ID', user?.employeeId || '—'],
    ['Email', user?.email || '—'],
    ['Role', formatRoleLabel(user?.role) || 'Admin'],
    ['Department', user?.departmentName || user?.department?.name || '—'],
    ['Account status', user?.isActive === false ? 'Inactive' : 'Active'],
  ];

  return (
    <div className="card border-0 shadow-sm">
      <div className="card-body p-4">
        <h1 className="h3 mb-1">Admin Profile</h1>
        <p className="text-muted mb-4">Your account details.</p>
        <dl className="row mb-0">
          {fields.map(([label, value]) => (
            <div className="col-md-6 mb-3" key={label}>
              <dt className="small text-muted fw-normal">{label}</dt>
              <dd className="mb-0 fw-semibold">{value}</dd>
            </div>
          ))}
        </dl>
      </div>
    </div>
  );
};

export default AdminProfilePage;
