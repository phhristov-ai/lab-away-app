import { useTranslation } from 'react-i18next';
// @ts-ignore
import './NavLinks.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHouse, faShop, faNewspaper, faXmark, faTruck, faEnvelope, faFlaskVial } from '@fortawesome/free-solid-svg-icons'
import { NavItem } from './NavItem';
import { useMobileSidebar } from '../../hooks/utils/useMobileSidebar';

interface NavLinksProps {
  isMobileSidebarOpen?: boolean;
  onClose?: () => void;
  showIcons?: boolean;
}

const NavLinks: React.FC<NavLinksProps> = ({ isMobileSidebarOpen = false, onClose, showIcons = false }) => {
  const { t, i18n } = useTranslation();
  const sidebarTop = useMobileSidebar(isMobileSidebarOpen);

  return (
    <ul
      className={`navbar-links ${isMobileSidebarOpen ? 'mobile-sidebar' : ''}`}
      style={isMobileSidebarOpen ? { top: `${sidebarTop}px` } : {}}
    >
      {isMobileSidebarOpen && (
        <NavItem
          icon={<FontAwesomeIcon icon={faXmark} className="nav-icon" />}
          onClick={onClose}
        />

      )}
      <NavItem
        to="/"
        label={t('header.navigation.home')}
        icon={showIcons && <FontAwesomeIcon icon={faHouse} className="nav-icon" />}
        onClick={onClose}
      />

      <NavItem
        to="/shop"
        label={t('header.navigation.shop')}
        icon={showIcons && <FontAwesomeIcon icon={faShop} className="nav-icon" />}
        onClick={onClose}
      />

      <NavItem
        to={`/${i18n.language}#how-it-works-anchor`}
        label={t('header.navigation.howItWorks')}
        icon={showIcons && <FontAwesomeIcon icon={faFlaskVial} className="nav-icon" />}
        onClick={onClose}
        isHashLink
        scroll={(el) => {
          if (el) {
            setTimeout(() => {
              el.scrollIntoView({ behavior: 'smooth', block: 'start' })
            }, 100)
          }
        }}
      />

      <NavItem
        to="/blog"
        label={t('header.navigation.blog')}
        icon={showIcons && <FontAwesomeIcon icon={faNewspaper} className="nav-icon" />}
        onClick={onClose}
      />

      {isMobileSidebarOpen && (
        <NavItem
          to="/delivery"
          label={t('header.navigation.shipping')}
          icon={showIcons && <FontAwesomeIcon icon={faTruck} className="nav-icon" />}
          onClick={onClose}
        />
      )}

      {isMobileSidebarOpen && (
        <NavItem
          to="/contact"
          label={t('header.navigation.contact')}
          icon={showIcons && <FontAwesomeIcon icon={faEnvelope} className="nav-icon" />}
          onClick={onClose}
        />
      )}

    </ul>
  );
};

export default NavLinks;