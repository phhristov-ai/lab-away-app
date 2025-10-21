import { Trans, useTranslation } from 'react-i18next';
import './TextStyles.css';

const DeliveryPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('delivery.title')}</h1>
      <p className="pre-line-text">
        <Trans
          i18nKey="delivery.deliveryInfo"
          components={{
            email: <a href="mailto:sales@lab-away.com">sales@lab-away.com</a>
          }}
        />
      </p>
    </div>
  );
};

export default DeliveryPage;
