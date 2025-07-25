import React from 'react';
import './FeatureImage.css';
import Button from './Button';
import { Link } from 'react-router-dom';

interface FeatureImageProps {
  imageSrc: string;
  altText: string;
  buttonText: string;
  buttonLink: string;
}
  
const FeatureImage: React.FC<FeatureImageProps> = ({ imageSrc, altText, buttonText, buttonLink }) => {
  return (
    <div className="feature-image ">
      <img src={imageSrc} alt={altText} />
      <Link to={buttonLink}>
        <Button text={buttonText} className="feature-image-button" />
      </Link>
    </div>
  );
};

export default FeatureImage;
