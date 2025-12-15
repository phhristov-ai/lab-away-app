import React from 'react';
import './ImageTextSection.css';
import Button from './Button';
import { Link } from 'react-router-dom';

interface ImageTextSectionProps {
  smallSrc: string;
  mediumSrc: string;
  imageAlt: string;
  title: string;
  text: string;
  buttonText: string;
  reverse?: boolean;
  displayButton?: boolean;
  buttonLink: string;
}

const ImageTextSection: React.FC<ImageTextSectionProps> = ({
  smallSrc,
  mediumSrc,
  imageAlt,
  title,
  text,
  buttonText,
  reverse = false,
  displayButton = true,
  buttonLink
}) => {
  return (
    <section className={`image-text-section ${reverse ? 'reverse' : ''}`}>
      <div className="image-column">
        <img
          alt={imageAlt}
          loading="lazy"
          srcSet={`
            ${smallSrc} 480w,
            ${mediumSrc} 768w
          `}
          sizes="(max-width: 480px) 100vw, 
                 (max-width: 768px) 100vw"
        />
      </div>
      <div className="text-column">
        <h2>{title}</h2>
        <p>{text}</p>
        <Link to={buttonLink}>
          <Button className={`${!displayButton ? 'hideButton' : ''}`} text={buttonText} />
        </Link>
      </div>
    </section>
  );
};

export default ImageTextSection;
