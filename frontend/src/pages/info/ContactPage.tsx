import { Helmet } from "react-helmet";
import { useTranslation } from "react-i18next";
import './ContactPage.css';

const ContactPage = () => {
  const { t } = useTranslation(); 
  
  return (
    <div className="contact-page">

      <Helmet>
        <title>{t('contact.title')} | Lab-Away</title>
        <meta name="description" content={t('contact.metaDescription') || "Contact us for queries, support, or business inquiries."} />
        <meta property="og:title" content={t('contact.title')} />
        <meta property="og:description" content={t('contact.metaDescription') || "Contact us for queries, support, or business inquiries."} />
      </Helmet>

      <h1>{t('contact.title')}</h1>

      <div className="contact-blocks">
        <div className="contact-block">
          <h2>{t('contact.hospital.label')}</h2>
          <p>{t('contact.hospital.name')}</p>
          <p>{t('contact.hospital.address.street')}</p>
          <p>{t('contact.hospital.address.city')}, {t('contact.hospital.address.postalCode')}</p>
          <p>{t('contact.hospital.address.country')}</p>
          <p>{t('contact.hospital.phone')}</p>
        </div>

        <div className="contact-block">
          <h2>{t('contact.mail.label')}</h2>
          <p>{t('contact.mail.description')}</p>
          <p><a href={`mailto:${t('contact.mail.email')}`}>{t('contact.mail.email')}</a></p>
        </div>

        <div className="contact-block">
          <h2>{t('contact.corporateBuilding.label')}</h2>
          <p>{t('contact.corporateBuilding.description')}</p>
        </div>
      </div>
    </div>
  );
};

export default ContactPage;
