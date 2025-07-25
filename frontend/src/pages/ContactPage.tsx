import { useTranslation } from "react-i18next";

const ContactPage = () => {
  const { t } = useTranslation(); 
  
  return (
    <div>
      <h1>{t('contact.title')}</h1>
      <div style={{ display: 'flex', gap: '20px' }}>
        <div style={{ flex: 1, border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
          <h2>{t('contact.hospital.label')}</h2>
          <p>{t('contact.hospital.name')}</p>
          <p>{t('contact.hospital.address.street')}</p>
          <p>{t('contact.hospital.address.city')}, {t('contact.hospital.address.postalCode')}</p>
          <p>{t('contact.hospital.address.country')}</p>
          <p>{t('contact.hospital.phone')}</p>
        </div>

        <div style={{ flex: 1, border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
          <h2>{t('contact.mail.label')}</h2>
          <p>{t('contact.mail.description')}</p>
          <p><a href={`mailto:${t('contact.mail.email')}`}>{t('contact.mail.email')}</a></p>
        </div>

        <div style={{ flex: 1, border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
          <h2>{t('contact.corporateBuilding.label')}</h2>
          <p>{t('contact.corporateBuilding.description')}</p>
        </div>
      </div>
    </div>
  );
};

export default ContactPage;
