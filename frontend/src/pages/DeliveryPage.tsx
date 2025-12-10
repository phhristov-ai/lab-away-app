import { useTranslation } from 'react-i18next';
import './TextStyles.css';
import { SUPPORT_EMAIL } from '../config/constants';

const DeliveryPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('delivery.title')}</h1>

      <section>
        <h3>{t('delivery.sections.areaTitle')}</h3>
        <p>{t('delivery.sections.areaText')}</p>
      </section>

      <section>
        <h3>{t('delivery.sections.shippingTitle')}</h3>
        <p>{t('delivery.sections.shippingText')}</p>
      </section>

      <section>
        <h3>{t('delivery.sections.timesTitle')}</h3>
        <p>{t('delivery.sections.timesText')}</p>
      </section>

      <section>
        <h3>{t('delivery.sections.combinedTitle')}</h3>
        <p>{t('delivery.sections.combinedText')}</p>
      </section>

      <section>
        <h3>{t('delivery.sections.contactTitle')}</h3>
        <p>
          {t('delivery.sections.contactText')}{' '}
          <a href={`mailto:${SUPPORT_EMAIL}`}>{SUPPORT_EMAIL}</a>
        </p>
      </section>
    </div>
  );
};

export default DeliveryPage;
