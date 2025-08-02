import { Link } from 'react-router-dom';
import CompanyLogo from '../../assets/images/header/lab-away.logo.png';
import './Logo.css';

const Logo = () => {
  return (
    <Link to="/" className="logo-link">
      <img src={CompanyLogo} alt="Lab-Away Logo" className="logo" />
    </Link>
  );
};

export default Logo;