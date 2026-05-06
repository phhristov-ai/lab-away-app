import visaIcon from '../../assets/icons/Visa.svg';
import mastercardIcon from '../../assets/icons/MasterCard.svg';
import paypalIcon from '../../assets/icons/PayPal.svg';
import amexIcon from '../../assets/icons/Amex.svg';
import sepaIcon from '../../assets/icons/Sepa.svg';
import giropayIcon from '../../assets/icons/GiroPay.svg';
import sofortIcon from '../../assets/icons/Sofort.svg';
import dhlIcon from '../../assets/icons/DHL.svg';

import './FooterBottom.css';
import { useTranslation } from 'react-i18next';

const FooterBottom = () => {
  const { t } = useTranslation();
  const year = new Date().getFullYear();
  return (
    <div className="footer-bottom-wrapper">
      <div className="footer-bottom">
        <div className="footer-bottom-left">
          {t('footer.copyright', { year })}
        </div>
        <div className="footer-bottom-right">
          <img
            src={visaIcon}
            alt={t('footer.payments.methods.visa')}
            loading="lazy"
            width="70"
            height="24"
          />
          <img
            src={mastercardIcon}
            alt={t('footer.payments.methods.mastercard')}
            loading="lazy"
            width="38"
            height="24"
          />
          <img
            src={paypalIcon}
            alt={t('footer.payments.methods.paypal')}
            loading="lazy"
            width="81"
            height="24"
          />
          <img
            src={amexIcon}
            alt={t('footer.payments.methods.amex')}
            loading="lazy"
            width="62"
            height="24"
          />
          <img
            src={sepaIcon}
            alt={t('footer.payments.methods.sepa')}
            loading="lazy"
            width="66"
            height="24"
          />
          <img
            src={giropayIcon}
            alt={t('footer.payments.methods.giropay')}
            loading="lazy"
            width="53"
            height="24"
          />
          <img
            src={sofortIcon}
            alt={t('footer.payments.methods.sofort')}
            loading="lazy"
            width="33"
            height="24"
          />
          <img
            src={dhlIcon}
            alt={t('footer.payments.methods.dhl')}
            loading="lazy"
            width="37"
            height="24"
          />
        </div>
      </div>
    </div>
  );
};

export default FooterBottom;
