import { Trans, useTranslation } from "react-i18next";
import '../styles/TextStyles.css'; 
import { SUPPORT_EMAIL } from "../../config/constants";
import { Helmet } from "react-helmet";

const TermsConditionsPage = () => {
  const { t } = useTranslation();

  const renderParagraphs = (path: string) => {
    const paragraphs = getArray(t(path, { returnObjects: true }));
    return paragraphs.map((_, idx) => (
      <p key={idx}>
        <Trans 
          i18nKey={`${path}.${idx}`}
          components={{ email: <a href={`mailto:${SUPPORT_EMAIL}`}>{SUPPORT_EMAIL}</a> }}
        />
      </p>
    ));
  };

  const renderList = (path: string) => {
    const items = getArray(t(path, { returnObjects: true }));
    return items.map((item, idx) => <li key={idx}>{item}</li>);
  };

  const renderForm = (path: string) => {
    const lines = getArray(t(path, { returnObjects: true }));
    return lines.map((line, idx) =>
      line.trim() === "" ? (
        <br key={idx} />
      ) : (
        <p key={idx}>
          <Trans 
            i18nKey={`${path}.${idx}`}
            components={{ email: <a href={`mailto:${SUPPORT_EMAIL}`}>{SUPPORT_EMAIL}</a> }}
          />
        </p>
      )
    );
  };

  const getArray = (result: any): string[] => {
    if (Array.isArray(result)) return result;
    if (typeof result === "string") return [result];
    return [];
  };

  return (
    <div>

      <Helmet>
        <title>{t('terms.title')} | Lab-Away</title>
        <meta name="description" content={t('terms.metaDescription') || "Read our terms and conditions for using our services."} />
        {/* Optional: Open Graph meta tags for better social sharing */}
        <meta property="og:title" content={t('terms.title')} />
        <meta property="og:description" content={t('terms.metaDescription') || "Read our terms and conditions for using our services."} />
      </Helmet>
      
      <h1>{t('terms.title')}</h1>

      <section>
        <h2>{t('terms.section1.title')}</h2>
        {renderParagraphs('terms.section1.paragraphs')}
      </section>

      <section>
        <h2>{t('terms.section2.title')}</h2>
        {renderParagraphs('terms.section2.paragraphs')}
      </section>

      <section>
        <h2>{t('terms.section3.title')}</h2>
        {renderParagraphs('terms.section3.paragraphs')}
        <ul>{renderList('terms.section3.list')}</ul>
        {renderParagraphs('terms.section3.paragraphsAfterList')}
      </section>

      <section>
        <h2>{t("terms.section4.title")}</h2>
        {renderParagraphs("terms.section4.paragraphs")}
      </section>

      <section>
        <h2>{t("terms.section5.title")}</h2>
        {renderParagraphs("terms.section5.paragraphs")}
        <ul>{renderList("terms.section5.list")}</ul>
        {renderParagraphs("terms.section5.paragraphsAfterList")}
      </section>

      <section>
        <h2>{t("terms.section6.title")}</h2>
        {renderParagraphs("terms.section6.paragraphs")}
        <h3>{t("terms.section6.subTitle")}</h3>
        <ul>{renderList("terms.section6.list")}</ul>
      </section>

      <section>
        <h2>{t("terms.section7.title")}</h2>
        {renderForm("terms.section7.form")}
      </section>

    </div>
  );
};

export default TermsConditionsPage;
