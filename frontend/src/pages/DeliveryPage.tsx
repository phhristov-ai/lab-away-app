import { Trans, useTranslation } from 'react-i18next';

const DeliveryPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('delivery.title')}</h1>
      <p style={{ whiteSpace: 'pre-line' }}>
        <Trans
          i18nKey="delivery.deliveryInfo"
          components={{ email: <a href="mailto:sales@lab-away.com" /> }}
        />
      </p>
    </div>
  );
};

export default DeliveryPage;
