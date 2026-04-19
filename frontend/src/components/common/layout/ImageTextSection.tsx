import React from 'react';
import './ImageTextSection.css';
import Button from '../../homepage/Button';
import { Link } from 'react-router-dom';


export type Media =
  | { type: 'image'; src: string }
  | { type: 'video'; src: string; mime?: string };

interface ImageTextSectionProps {
  media: Media;
  title: string;
  text: string;
  buttonText: string;
  reverse?: boolean;
  displayButton?: boolean;
  buttonLink: string;
  isAdmin?: boolean;
  onMediaChange?: (file: File) => void;
}

const ImageTextSection: React.FC<ImageTextSectionProps> = ({
  media,
  title,
  text,
  buttonText,
  reverse = false,
  displayButton = true,
  buttonLink,
  isAdmin = false,
  onMediaChange
}) => {
  const inputRef = React.useRef<HTMLInputElement>(null);
  const renderMedia = () => {

    if (media.type === 'image') {
      return (
        <img
          className="media"
          alt={media.src}
          loading="lazy"
          srcSet={`
            ${media.src} 480w,
            ${media.src} 768w
          `}
          sizes="(max-width: 480px) 100vw,
                 (max-width: 768px) 100vw"
        />
      );
    }

    return (
      <video
        className="media"
        src={media.src}
        autoPlay
        muted
        loop
        playsInline
        controls
      />
    );
  };

  return (
    <section className={`image-text-section ${reverse ? 'reverse' : ''}`}>
      <div className="image-column admin-wrapper">
        {renderMedia()}

        {isAdmin && (
          <>
            <input
              ref={inputRef}
              type="file"
              accept="image/*,video/*"
              className="hidden-file-input"
              onChange={(e) => {
                const file = e.target.files?.[0];
                if (file) {
                  onMediaChange?.(file);
                }
              }}
            />

            <div
              className="admin-click-layer"
              onClick={() => inputRef.current?.click()}
            />
          </>
        )}
      </div>

      <div className="text-column">
        <h2>{title}</h2>
        <p>{text}</p>

        {displayButton && (
          <Link to={buttonLink}>
            <Button text={buttonText} />
          </Link>
        )}
      </div>
    </section>
  );
};

export default ImageTextSection;