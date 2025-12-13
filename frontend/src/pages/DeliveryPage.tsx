import { useTranslation } from 'react-i18next';
import './TextStyles.css';
import { SUPPORT_EMAIL } from '../config/constants';
import { Helmet } from 'react-helmet';

const DeliveryPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <Helmet>
        <title>{t('delivery.title')} | Lab-Away</title>
        <meta name="description" content={t('delivery.metaDescription') || "Delivery and shipping details for our services."} />
        <meta property="og:title" content={t('delivery.title')} />
        <meta property="og:description" content={t('delivery.metaDescription') || "Delivery and shipping details for our services."} />
      </Helmet>
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
