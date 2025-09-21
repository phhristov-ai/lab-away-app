import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
// @ts-ignore
import { HashLink } from 'react-router-hash-link';
import './NavLinks.css';
import { useEffect } from 'react';

const NavLinks = ({ isMobileSidebarOpen = false, onClose }: { isMobileSidebarOpen?: boolean; onClose?: () => void }) => {
  const { t } = useTranslation();

  useEffect(() => {
    if (isMobileSidebarOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }

    return () => {
      document.body.style.overflow = '';
    };
  }, [isMobileSidebarOpen]);

  const handleLinkClick = () => {
    if (onClose) onClose();
  };

  return (
    <ul className={`navbar-links ${isMobileSidebarOpen ? 'mobile-sidebar' : ''}`}>
      {isMobileSidebarOpen && (
        <li className="close-button-mobile">
          <button onClick={onClose}>×</button>
        </li>
      )}
      <li><Link to="/" onClick={handleLinkClick}>{t('header.navigation.home')}</Link></li>
      <li><Link to="/shop" onClick={handleLinkClick}>{t('header.navigation.shop')}</Link></li>
      <li>
        <HashLink smooth to="/#how-it-works" onClick={handleLinkClick}>{t('header.navigation.howItWorks')}</HashLink>
      </li>
      <li><Link to="/blog" onClick={handleLinkClick}>{t('header.navigation.blog')}</Link></li>
    </ul>
  );
};

export default NavLinks;
