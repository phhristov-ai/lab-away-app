import { useTranslation } from 'react-i18next';
import flagEU from '../../assets/icons/org-eu-wave.svg';
import './Navbar.css';

const TopHeader = () => {
  const { t } = useTranslation();

  return (
    <div className="top-header">
      <img src={flagEU} alt="EU flag" className="top-header-flag" />
      <p>{t('header.topHeader')}</p>
    </div>
  );
};

export default TopHeader;
