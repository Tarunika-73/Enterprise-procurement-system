import { useNavigate } from 'react-router-dom';
import Logo from '../../components/Authentication/Logo/Logo';
import Button from '../../components/Authentication/Button/Button';
import { useAuth } from '../../context/AuthContext';

const Unauthorized = () => {
  const navigate = useNavigate();
  const { logout, userRole } = useAuth();

  return (
    <div className="auth-status-page">
      <div className="auth-status-card">
        <Logo size="sm" className="justify-content-center mb-4" />
        <div className="auth-status-code mb-3">403</div>
        <h1 className="h3 fw-bold mb-3">Access Denied</h1>
        <p className="text-muted mb-4">
          {userRole
            ? `Your role (${userRole}) does not have permission to access this resource.`
            : 'You do not have permission to access this page. Please contact your administrator.'}
        </p>
        <div className="d-flex flex-column flex-sm-row gap-3 justify-content-center">
          <Button variant="primary" fullWidth={false} className="px-4" onClick={() => navigate('/')}>
            Go to Login
          </Button>
          <Button variant="outline-primary" fullWidth={false} className="px-4" onClick={logout}>
            Sign Out
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Unauthorized;
