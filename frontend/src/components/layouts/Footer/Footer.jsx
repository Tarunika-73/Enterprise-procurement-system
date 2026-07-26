import { APP_NAME, APP_VERSION, COPYRIGHT } from '../../../utils/constants';

const Footer = () => {
  return (
    <footer className="dashboard-footer">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-center gap-2 text-center text-md-start">
        <span>{APP_NAME}</span>
        <span>Version {APP_VERSION}</span>
        <span>{COPYRIGHT}</span>
      </div>
    </footer>
  );
};

export default Footer;
