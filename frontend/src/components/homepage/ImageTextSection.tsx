import React from 'react';
import './ImageTextSection.css';
import Button from './Button';
import { Link } from 'react-router-dom';


interface ImageTextSectionProps {
  imageSrc: string;
  imageAlt: string;
  title: string;
  text: string;
  buttonText: string;
  reverse?: boolean;
  displayButton?: boolean;
  buttonLink: string;
}

const ImageTextSection: React.FC<ImageTextSectionProps> = ({ imageSrc, imageAlt, title, text, buttonText, reverse = false, displayButton = true, buttonLink }) => {
  return (
    <section className={`image-text-section ${reverse ? 'reverse' : ''}`}>
    <div className="image-column">
      <img src={imageSrc} alt={imageAlt} />
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
