import HeroSection from '../components/homepage/HeroSection';
import FeatureImages from '../components/homepage/FeatureImages';
import ImageTextSection from '../components/homepage/ImageTextSection';
import FeaturedImage4 from '../assets/images/FeaturedImage4.jpg';
import FeaturedImage5 from '../assets/images/FeaturedImage5.jpg';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import ScrollAnimatedArrows from '../components/homepage/ScrollAnimatedArrows';
import { useLocation } from 'react-router-dom';
import RandomProductRow from '../components/common/RandomProducts';
import './HomePage.css';

const HomePage = () => {
  const { t } = useTranslation();
  const location = useLocation();

  useEffect(() => {
    if (location.hash) {
      const element = document.getElementById(location.hash.replace('#', ''));
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }, [location]);

  return (
    <div>
      <HeroSection />
      <FeatureImages />

      <ImageTextSection
        imageSrc={FeaturedImage4}
        imageAlt={t('homepage.firstParagraph.alt', 'No sample')}
        title={t('homepage.firstParagraph.title')}
        text={t('homepage.firstParagraph.text')}
        buttonText={t('homepage.hero.shopButton')}
        buttonLink="/shop"
      />
      <h1 className="centered-heading">
        {t('homepage.randomProductRow.title')}
      </h1>
      <RandomProductRow />

      <ScrollAnimatedArrows />

      <RandomProductRow />

      <ImageTextSection
        imageSrc={FeaturedImage5}
        imageAlt={t('homepage.secondParagraph.text')}
        title={t('homepage.secondParagraph.title')}
        text={t('homepage.secondParagraph.text')}
        buttonText={t('homepage.hero.shopButton')}
        buttonLink="/shop"
      />

    </div>
  );
};

export default HomePage;