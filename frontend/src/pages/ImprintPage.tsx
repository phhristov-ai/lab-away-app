import { useTranslation } from "react-i18next";
import { COMPANY_ADDRESS, EU_ODR_URL, MANAGER_EMAIL, MANAGER_NAME, PHONE_NUMBER, SITE_NAME, SUPPORT_EMAIL, UIC, VAT_ID } from "../config/constants";

const ImprintPage = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t("imprint.title")}</h1>

      <p>
        <a href={`https://${SITE_NAME}`} target="_blank" rel="noopener noreferrer">
          {SITE_NAME}
        </a>{" "}
        {t("imprint.description")}
      </p>

      <h2>{t("imprint.company.name")}</h2>

      <address>
        <p>{COMPANY_ADDRESS.street}</p>
        <p>
          {COMPANY_ADDRESS.postalCode} {COMPANY_ADDRESS.city}
        </p>
        <p>{COMPANY_ADDRESS.country}</p>
      </address>

      <p>{t("imprint.company.registration", { UIC })}</p>
      <p>{t("imprint.company.vatId", { VAT_ID })}</p>

      <p>
        E-Mail:{" "}
        <a href={`mailto:${SUPPORT_EMAIL}`}>{SUPPORT_EMAIL}</a>
      </p>

      <p>
        Telefon: <a href={`tel:${PHONE_NUMBER}`}>{PHONE_NUMBER}</a>
      </p>

      <h3>{MANAGER_NAME}</h3>
      <p>
        <a href={`mailto:${MANAGER_EMAIL}`}>{MANAGER_EMAIL}</a>
      </p>

      <section>
        <h2>{t("imprint.legalNotice.title")}</h2>
        <p>{t("imprint.legalNotice.text1")}</p>
        <p>  
          <a href={EU_ODR_URL} target="_blank" rel="noopener noreferrer">
          {EU_ODR_URL}
        </a>
        </p>
        <p>{t("imprint.legalNotice.text2")}</p>
      </section>
    </div>
  );
};

export default ImprintPage;
