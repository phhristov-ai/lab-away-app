import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
// @ts-ignore
import { HashLink } from 'react-router-hash-link';
import './NavLinks.css';

const NavLinks = ({ isMobileSidebarOpen = false, onClose }: { isMobileSidebarOpen?: boolean; onClose?: () => void }) => {
  const { t } = useTranslation();

  return (
    <ul className={`navbar-links ${isMobileSidebarOpen ? 'mobile-sidebar' : ''}`}>
      {isMobileSidebarOpen && (
        <li className="close-button-mobile">
          <button onClick={onClose}>×</button>
        </li>
      )}
      <li><Link to="/">{t('header.navigation.home')}</Link></li>
      <li><Link to="/shop">{t('header.navigation.shop')}</Link></li>
      <li>
        <HashLink smooth to="/#how-it-works">{t('header.navigation.howItWorks')}</HashLink>
      </li>
      <li><Link to="/blog">{t('header.navigation.blog')}</Link></li>
    </ul>
  );
};

export default NavLinks;
