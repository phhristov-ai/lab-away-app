import { useTranslation } from "react-i18next";

const PrivacyPolicyPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t("privacy.title")}</h1>
      <p style={{ whiteSpace: "pre-line" }}>
        {t("privacy.privacyPolicy")}
      </p>
    </div>
  );
};

export default PrivacyPolicyPage;