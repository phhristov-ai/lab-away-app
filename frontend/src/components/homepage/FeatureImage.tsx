import React from 'react';
import { Link } from 'react-router-dom';
import Button from './Button';
import './FeatureImage.css';

interface FeatureImageProps {
  smallSrc: string;
  mediumSrc: string;
  altText: string;
  buttonText: string;
  buttonLink: string;
}

const FeatureImage: React.FC<FeatureImageProps> = ({
  smallSrc,
  mediumSrc,
  altText,
  buttonText,
  buttonLink,
}) => {
  return (
    <Link to={buttonLink} className="feature-image-link">
      <div className="feature-image">
        <img
          src={mediumSrc}
          alt={altText}
          loading="lazy"
          srcSet={`
            ${mediumSrc} 768w,
            ${smallSrc} 480w
          `}
          sizes="(max-width: 480px) 480px,
                 (max-width: 768px) 768px,
                 768px"
        />
        <Button text={buttonText} className="feature-image-button" />
      </div>
    </Link>
  );
};

export default FeatureImage;