import './FooterAbout.css';
import { useTranslation } from 'react-i18next';


const FooterAbout = () => {
  const { t } = useTranslation();
  return (
    <div className="footer-about">
      <p>{t('footer.about.text')}</p>
    </div>
  );
};

export default FooterAbout;