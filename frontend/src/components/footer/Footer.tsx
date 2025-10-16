import LinksList from './LinksList';
import FooterSubscribe from './FooterSubscribe';
import './Footer.css';
import FooterAbout from './FooterAbout';
import FooterBottom from './FooterBottom';
import Logo from '../header/Logo';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import ShortFooter from './ShortFooter';

const Footer = () => {
  const { t } = useTranslation();
  const location = useLocation();

  const shopLinks = [
    { label: 'Drug Tests', url: '/product/drug-tests' },
    { label: 'Fertility Tests', url: '/product/fertility-tests' },
    { label: 'STI/STD Tests', url: '/product/stistd-tests' },
    { label: 'Ovulation Test', url: '/product/ovulation-test' },
    { label: 'Vitamin D Test', url: '/product/vitamin-d-test' },
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


  const isCheckoutPage = ['/checkout', '/payment', '/success', '/cart'].some(path =>
    location.pathname.includes(path)
  );

  if (isCheckoutPage) {
    return <ShortFooter />;
  }

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