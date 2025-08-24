import { useTranslation } from "react-i18next";
import './TextStyles.css'; 

const PrivacyPolicyPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t("privacy.title")}</h1>
      <p className="pre-line-text">
        {t("privacy.privacyPolicy")}
      </p>
    </div>
  );
};

export default PrivacyPolicyPage;