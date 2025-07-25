import './HeroSection.css';
import { useTranslation } from 'react-i18next';

const HeroSection = () => {
  const { t } = useTranslation();

  return (
    <section className="hero-section">
      <h1 className="hero-title">{t('homepage.hero.mainTitle')}</h1>
      <p className="hero-subtitle">{t('homepage.hero.subtitle')}</p>
    </section>
  );
};

export default HeroSection;
