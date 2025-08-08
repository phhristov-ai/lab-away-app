import LinksList from './LinksList';
import FooterSubscribe from './FooterSubscribe';
import './Footer.css';
import FooterAbout from './FooterAbout';
import FooterBottom from './FooterBottom';
import Logo from '../header/Logo';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

const Footer = () => {
  const { t } = useTranslation();
  const shopLinks = [
    { label: 'Drug Tests', url: '#' },
    { label: 'Fertility Tests', url: '#' },
    { label: 'STI/STD Tests', url: '#' },
    { label: 'Ovulation Test', url: '#' },
    { label: 'Vitamin D Test', url: '#' },
  ];

  const quickLinks = [
    { label: t('footer.links.main.home'), url: '/' },
    { label: t('footer.links.main.shop'), url: '/shop' },
    { label: t('footer.links.main.howItWorks'), url: '#how-it-works' },
    { label: t('footer.links.main.blog'), url: '/blog' },
  ];

  const supportLinks = [
    { label: t('footer.links.legal.delivery'), url: '/delivery' },
    { label: t('footer.links.legal.privacy'), url: '/privacy-policy' },
    { label: t('footer.links.legal.terms'), url: '/terms-and-conditions' },
    { label: t('footer.links.contact.contact'), url: '/contact' },
    { label: t('footer.links.legal.imprint'), url: '/imprint' },
  ];

  return (
    <footer className="footer">
      <div className="footer-inner">
        <div className="footer-row">
          <div className="footer-column footer-column-logo">
            <Logo />
          </div>
          <div className="footer-column footer-column-shop">
            <LinksList title={t('footer.titles.shop')} links={shopLinks} />
          </div>
          <div className="footer-column footer-column-quicklinks">
            <LinksList title={t('footer.titles.quickLinks')} links={quickLinks} />
          </div>
          <div className="footer-column footer-column-support">
            <h3>{t('footer.titles.support')}</h3>
            <ul>
              {supportLinks.map(({ label, url }) => (
                <li key={url}>
                  <Link to={url}>{label}</Link>
                </li>
              ))}
            </ul>
          </div>
          <div className="footer-column footer-column-subscribe">
            <FooterSubscribe />
          </div>
        </div>
        <div className="footer-row footer-row-about">
          <FooterAbout />
        </div>
        <div className="footer-row footer-row-bottom">
          <FooterBottom />
        </div>
      </div>
    </footer>
  );
};

export default Footer;