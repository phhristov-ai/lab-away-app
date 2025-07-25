import { t } from 'i18next';
import FeaturedImage1 from '../../assets/images/home/FeaturedImage1.jpeg';
import FeaturedImage2 from '../../assets/images/home/FeaturedImage2.webp';
import FeaturedImage3 from '../../assets/images/home/FeaturedImage3.webp';
import FeatureImage from './FeatureImage';
import './FeatureImages.css';


const FeatureImages = () => {
  return (
    <div className="feature-images">
      <FeatureImage
        imageSrc={FeaturedImage1}
        altText="STI/STD Tests"
        buttonText={t('homepage.featuredButtons.sti')}
        buttonLink="/product/std-bundle"
      />
      <FeatureImage
        imageSrc={FeaturedImage2}
        altText="Drug Tests"
        buttonText={t('homepage.featuredButtons.drug')}
        buttonLink="/product/drugs-bundle"
      />
      <FeatureImage
        imageSrc={FeaturedImage3}
        altText="Fertility Tests"
        buttonText={t('homepage.featuredButtons.fertility')}
        buttonLink="/product/ovulation-test"
      />
    </div>
  );
};

export default FeatureImages;