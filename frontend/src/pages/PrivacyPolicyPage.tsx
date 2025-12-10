import { useTranslation } from "react-i18next";
import './TextStyles.css'; 

const PrivacyPolicyPage = () => {
  const { t } = useTranslation();

  const getArray = (result: any): string[] => {
    if (Array.isArray(result)) return result;
    if (typeof result === "string") return [result];
    return [];
  };

  const renderParagraphs = (path: string) => {
    const paragraphs = getArray(t(path, { returnObjects: true }));
    return paragraphs.map((p, idx) => <p key={idx}>{p}</p>);
  };

  const renderList = (path: string) => {
    const items = getArray(t(path, { returnObjects: true }));
    if (items.length === 0) return null;
    return (
      <ul>
        {items.map((item, idx) => (
          <li key={idx}>{item}</li>
        ))}
      </ul>
    );
  };

  return (
    <div>
      <h1>{t("privacy.title")}</h1>

      <section>
        <h2>{t("privacy.section1.title")}</h2>
        {renderParagraphs("privacy.section1.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section2.title")}</h2>
        {renderParagraphs("privacy.section2.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section3.title")}</h2>
        {renderParagraphs("privacy.section3.paragraphs")}
        {renderList("privacy.section3.list")}
      </section>

      <section>
        <h2>{t("privacy.section4.title")}</h2>
        {renderParagraphs("privacy.section4.paragraphs")}
        {renderList("privacy.section4.list")}
      </section>

      <section>
        <h2>{t("privacy.section5.title")}</h2>
        {renderParagraphs("privacy.section5.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section6.title")}</h2>
        {renderParagraphs("privacy.section6.paragraphs")}
        {renderList("privacy.section6.list")}
      </section>

      <section>
        <h2>{t("privacy.section7.title")}</h2>
        {renderParagraphs("privacy.section7.paragraphs")}
        {renderList("privacy.section7.list")}
      </section>

      <section>
        <h2>{t("privacy.section8.title")}</h2>
        {renderParagraphs("privacy.section8.paragraphs")}
        {renderList("privacy.section8.list")}
      </section>

      <section>
        <h2>{t("privacy.section9.title")}</h2>
        {renderParagraphs("privacy.section9.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section10.title")}</h2>
        {renderParagraphs("privacy.section10.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section11.title")}</h2>
        {renderParagraphs("privacy.section11.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section12.title")}</h2>
        {renderParagraphs("privacy.section12.paragraphs")}
      </section>

      <section>
        <h2>{t("privacy.section13.title")}</h2>
        {renderParagraphs("privacy.section13.paragraphs")}
      </section>
    </div>
  );
};

export default PrivacyPolicyPage;
