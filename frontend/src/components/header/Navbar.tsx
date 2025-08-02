import Logo from './Logo';
import NavLinks from './NavLinks';
import LanguageDropdown from './LanguageDropdown';
import './Navbar.css';
import CartIconWrapper from './CartIconWrapper';
import { useEffect, useState } from 'react';
import { useAdmin } from '../../context/AdminContext';
import logoutIcon from '../../assets/icons/logout-icon.svg';
import Hamburger from './Hamburger';

const Navbar = () => {
  const { isAdmin, logout } = useAdmin();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
    };

    window.addEventListener('resize', handleResize);

    handleResize();

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return (
    <>
      <nav className="navbar">
        <div className="navbar-inner">
          <div className="navbar-left">
            {isMobile ? (
              <>
                <Hamburger onClick={() => setSidebarOpen(true)} isOpen={sidebarOpen} />
                <Logo />
              </>
            ) : (
              <>
                <Logo />
                <NavLinks />
              </>
            )}


          </div>

          <div className="navbar-right">
            <LanguageDropdown />
            <CartIconWrapper />
            {isAdmin && (
              <button onClick={logout} className="icon-button" title="Logout">
                <img src={logoutIcon} alt="Logout" />
              </button>
            )}
          </div>
        </div>
      </nav>

      {isMobile && sidebarOpen && (
        <NavLinks isMobileSidebarOpen={true} onClose={() => setSidebarOpen(false)} />
      )}
    </>
  );
};

export default Navbar;