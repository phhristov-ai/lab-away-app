import FeaturedImage1 from '../../assets/images/home/FeaturedImage1.jpeg';
import FeaturedImage2 from '../../assets/images/home/FeaturedImage2.webp';
import FeaturedImage3 from '../../assets/images/home/FeaturedImage3.webp';
import FeatureImage from './FeatureImage';
import './FeatureImages.css';
import { useTranslation } from 'react-i18next';

const FeatureImages = () => {
  const { t, i18n } = useTranslation();
  const lang = i18n.language || 'en';

  return (
    <div className="feature-images">
      <FeatureImage
        imageSrc={FeaturedImage1}
        altText="STI/STD Tests"
        buttonText={t('homepage.featuredButtons.sti')}
        buttonLink={`/${lang}/product/stistd-tests`}
      />
      <FeatureImage
        imageSrc={FeaturedImage2}
        altText="Drug Tests"
        buttonText={t('homepage.featuredButtons.drug')}
        buttonLink={`/${lang}/product/drug-tests`}
      />
      <FeatureImage
        imageSrc={FeaturedImage3}
        altText="Fertility Tests"
        buttonText={t('homepage.featuredButtons.fertility')}
        buttonLink={`/${lang}/product/fertility-tests`}
      />
    </div>
  );
};

export default FeatureImages;
