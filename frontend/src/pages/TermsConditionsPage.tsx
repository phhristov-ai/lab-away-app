import { useTranslation } from "react-i18next";

const TermsConditionsPage = () => {
  const { t } = useTranslation();
  return (
    <div>
      <h1>{t('terms.title')}</h1>
      <p style={{ whiteSpace: 'pre-line' }}>
        {t('terms.generalTermsAndConditions')}
      </p>
    </div>
  );
};

export default TermsConditionsPage;
