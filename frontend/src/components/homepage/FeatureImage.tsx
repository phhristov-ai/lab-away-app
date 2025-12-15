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
    <div className="feature-image">
      <img
        src={mediumSrc}
        alt={altText}
        loading="lazy"
        srcSet={`
          ${mediumSrc} 768w,
          ${smallSrc} 480w
        `}
        sizes="(max-width: 480x) 480px,
                (max-width: 768px) 768px,
                768px"
      />

      <Link to={buttonLink}>
        <Button text={buttonText} className="feature-image-button" />
      </Link>
    </div>
  );
};

export default FeatureImage;
