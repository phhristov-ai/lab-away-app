import HeroSection from '../../components/homepage/HeroSection';
import FeatureImages from '../../components/homepage/FeatureImages';
import ImageTextSection from '../../components/common/layout/ImageTextSection';
import FeaturedImage4Small from '../../assets/images/home/FeaturedImage4_480.webp';
import FeaturedImage4Medium from '../../assets/images/home/FeaturedImage4_768.webp';
import FeaturedImage5Small from '../../assets/images/home/FeaturedImage5_480.webp';
import FeaturedImage5Medium from '../../assets/images/home/FeaturedImage5_768.webp';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation } from 'react-router-dom';
import RandomProductRow from '../../components/common/product/RandomProducts';
import './HomePage.css';
import { Helmet } from 'react-helmet';
import ScrollAnimatedArrows from '../../components/homepage/ScrollAnimatedArrows';

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
      <Helmet>
        <title>Lab-Away | Home - High-Quality Home Health Test Kits</title>
        <meta 
          name="description" 
          content="Lab-Away provides high-quality home test kits for STI/STD tests, drug tests, and fertility tests with fast and discreet delivery. Get accurate results without the hassle of sending samples back." 
        />

        <script type="application/ld+json">
          {`
            {
              "@context": "https://schema.org",
              "@type": "WebPage",
              "name": "Lab-Away | Home",
              "description": "Lab-Away provides home test kits for STI/STD, drug, and fertility tests with discreet and fast delivery services.",
              "url": "https://www.lab-away.com"
            }
          `}
        </script>
      </Helmet>

      <HeroSection />
      <FeatureImages />
      
      <ImageTextSection
        smallSrc={FeaturedImage4Small}
        mediumSrc={FeaturedImage4Medium}
        imageAlt={t('homepage.firstParagraph.alt', 'No sample')}
        title={t('homepage.firstParagraph.title')}
        text={t('homepage.firstParagraph.text')}
        buttonText={t('homepage.hero.shopButton')}
        buttonLink="/shop"
      />
      <h2 className="centered-heading">
        {t('homepage.randomProductRow.title')}
      </h2>
      <RandomProductRow />

      <ScrollAnimatedArrows />
      <RandomProductRow />

      <ImageTextSection
        smallSrc={FeaturedImage5Small}
        mediumSrc={FeaturedImage5Medium}
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
