import { useTranslation } from "react-i18next";
import './TextStyles.css'; 

const TermsConditionsPage = () => {
  const { t } = useTranslation();
  return (
    <div>
      <h1>{t('terms.title')}</h1>
      <p className="pre-line-text">
        {t('terms.generalTermsAndConditions')}
      </p>
    </div>
  );
};

export default TermsConditionsPage;
