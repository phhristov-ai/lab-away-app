import { useTranslation } from 'react-i18next';
import SubscribeForm from './SubscribeForm';
import SocialMediaLinks from './SocialMediaLinks';
import FooterLine from './FooterLine';
import './FooterSubscribe.css';

const FooterSubscribe = () => {
  const { t } = useTranslation();

  return (
    <div className="footer-column footer-subscribe">
      <h4>{t('footer.subscribe.title')}</h4>
      <p>{t('footer.subscribe.description')}</p>

      <SubscribeForm />
      <FooterLine />
      <SocialMediaLinks />
    </div>
  );
};

export default FooterSubscribe;
