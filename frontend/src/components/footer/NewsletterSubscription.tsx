import { useTranslation } from "react-i18next";

const NewsletterSubscription = () => {
  const { t } = useTranslation();
  
  return <div className="logo">{t('newsletter.title')}</div>;
};

export default NewsletterSubscription;
