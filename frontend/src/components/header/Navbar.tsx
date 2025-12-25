import Logo from './Logo';
import NavLinks from './NavLinks';
import LanguageDropdown from './LanguageDropdown';
import './Navbar.css';
import CartIconWrapper from './CartIconWrapper';
import { useState } from 'react';
import { useAdmin } from '../../context/AdminContext';
import logoutIcon from '../../assets/icons/logout-icon.svg';
import Hamburger from './Hamburger';
import { useIsMobile } from '../../hooks/utils/useIsMobile';

const Navbar = () => {
  const { isAdmin, logout } = useAdmin()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const isMobile = useIsMobile();

  const closeSidebar = () => setSidebarOpen(false)
  const toggleSidebar = () => setSidebarOpen((prev) => !prev)

  return (
    <>
      <nav className="navbar">
        <div className="navbar-inner">
          <div className="navbar-left">
            {isMobile && (
              <Hamburger onClick={toggleSidebar} isOpen={sidebarOpen} />
            )}

            <Logo />

            {!isMobile && <NavLinks />}
          </div>

          <div className="navbar-right">
            <LanguageDropdown />
            <CartIconWrapper />

            {isAdmin && (
              <button
                onClick={logout}
                className="icon-button"
                title="Logout"
              >
                <img src={logoutIcon} alt="Logout" loading="lazy" />
              </button>
            )}
          </div>
        </div>
      </nav>

      {isMobile && sidebarOpen && (
        <NavLinks
          isMobileSidebarOpen
          onClose={closeSidebar}
          showIcons
        />
      )}
    </>
  )
}

export default Navbar
