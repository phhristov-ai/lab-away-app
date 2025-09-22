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
        <HashLink
          smooth
          to="/#how-it-works-anchor"
          scroll={(el: { scrollIntoView: (arg0: { behavior: string; block: string; }) => void; }) => {
            setTimeout(() => {
              el.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 100);
          }}
          onClick={onClose}
        >
          {t('header.navigation.howItWorks')}
        </HashLink>      </li>
      <li><Link to="/blog">{t('header.navigation.blog')}</Link></li>
    </ul>
  );
};

export default NavLinks;
