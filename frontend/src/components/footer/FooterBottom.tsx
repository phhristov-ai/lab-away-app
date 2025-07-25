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
  return (
    <div className="footer-bottom-wrapper">
      <div className="footer-bottom">
        <div className="footer-bottom-left">
          {t('footer.copyright')}
        </div>
        <div className="footer-bottom-right">
          <img src={visaIcon} alt={t('footer.payments.methods.visa')} />
          <img src={mastercardIcon} alt={t('footer.payments.methods.mastercard')} />
          <img src={paypalIcon} alt={t('footer.payments.methods.paypal')} />
          <img src={amexIcon} alt={t('footer.payments.methods.amex')} />
          <img src={sepaIcon} alt={t('footer.payments.methods.sepa')} />
          <img src={giropayIcon} alt={t('footer.payments.methods.giropay')} />
          <img src={sofortIcon} alt={t('footer.payments.methods.sofort')} />
          <img src={dhlIcon} alt={t('footer.payments.methods.dhl')} />
        </div>
      </div>
    </div>
  );
};

export default FooterBottom;
