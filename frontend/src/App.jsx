import { BrowserRouter } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import AuthRoutes from './routes/AuthRoutes';
import ProcurementAssistant from './components/ProcurementAssistant';

const AuthenticatedApplication = () => {
  const { isAuthenticated, user } = useAuth();
  const assistantIdentity = isAuthenticated && user?.id != null ? String(user.id) : 'signed-out';

  return (
    <>
      <AuthRoutes />
      <ProcurementAssistant key={assistantIdentity} />
    </>
  );
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AuthenticatedApplication />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
