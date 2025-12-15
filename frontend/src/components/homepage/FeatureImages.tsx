import { useTranslation } from 'react-i18next';
import FeatureImage from './FeatureImage';

// Image imports
import FeaturedImage1Small from '../../assets/images/home/FeaturedImage1_480.webp';
import FeaturedImage1Medium from '../../assets/images/home/FeaturedImage1_768.webp';

import FeaturedImage2Small from '../../assets/images/home/FeaturedImage2_480.webp';
import FeaturedImage2Medium from '../../assets/images/home/FeaturedImage2_768.webp';

import FeaturedImage3Small from '../../assets/images/home/FeaturedImage3_480.webp';
import FeaturedImage3Medium from '../../assets/images/home/FeaturedImage3_768.webp';

import './FeatureImages.css';

const FeatureImages = () => {
  const { t, i18n } = useTranslation();
  const lang = i18n.language || 'en';

  return (
    <div className="feature-images">
      <FeatureImage
        smallSrc={FeaturedImage1Small}
        mediumSrc={FeaturedImage1Medium}
        altText="STI/STD Tests"
        buttonText={t('homepage.featuredButtons.sti')}
        buttonLink={`/${lang}/product/stistd-tests`}
      />
      <FeatureImage
        smallSrc={FeaturedImage2Small}
        mediumSrc={FeaturedImage2Medium}
        altText="Drug Tests"
        buttonText={t('homepage.featuredButtons.drug')}
        buttonLink={`/${lang}/product/drug-tests`}
      />
      <FeatureImage
        smallSrc={FeaturedImage3Small}
        mediumSrc={FeaturedImage3Medium}
        altText="Fertility Tests"
        buttonText={t('homepage.featuredButtons.fertility')}
        buttonLink={`/${lang}/product/fertility-tests`}
      />
    </div>
  );
};

export default FeatureImages;
