import { useNavigate } from 'react-router-dom';
import Logo from '../../components/Authentication/Logo/Logo';
import Button from '../../components/Authentication/Button/Button';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="auth-status-page">
      <div className="auth-status-card">
        <Logo size="sm" className="justify-content-center mb-4" />
        <div className="auth-status-code mb-3">404</div>
        <h1 className="h3 fw-bold mb-3">Page Not Found</h1>
        <p className="text-muted mb-4">
          The page you are looking for does not exist or may have been moved.
          Please check the URL or return to the login page.
        </p>
        <Button variant="primary" fullWidth={false} className="px-5" onClick={() => navigate('/')}>
          Back to Login
        </Button>
      </div>
    </div>
  );
};

export default NotFound;
