import { useTranslation } from "react-i18next";

const ImprintPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('imprint.title')}</h1>

      <p>{t('imprint.description')}</p>

      <h2>{t('imprint.company.name')}</h2>
      <address>
        <p>{t('imprint.company.address.street')}</p>
        <p>
          {t('imprint.company.address.postalCode')} {t('imprint.company.address.city')}
        </p>
        <p>{t('imprint.company.address.country')}</p>
      </address>

      <p>{t('imprint.company.taxId')}</p>
      <p>{t('imprint.company.email')}</p>

      <h3>{t('imprint.company.manager.name')}</h3>
      <p>{t('imprint.company.manager.email')}</p>

      <section>
        <h2>{t('imprint.legalNotice.title')}</h2>
        <p>{t('imprint.legalNotice.text1')}</p>
        <p>{t('imprint.legalNotice.text2')}</p>
        <p>{t('imprint.legalNotice.text3')}</p>
      </section>
    </div>
  );
};

export default ImprintPage;
