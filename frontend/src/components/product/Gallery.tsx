import React from 'react';
import './Gallery.css';
import SlickDots from './SlickDots';
import { ProductImage } from '../../types/ProductImage';
import { useGallery } from '../../hooks/useGallery';

interface GalleryProps {
  images: ProductImage[];
  setImages: React.Dispatch<React.SetStateAction<ProductImage[]>>;
  isAdmin: boolean;
}

const Gallery: React.FC<GalleryProps> = ({ images, setImages, isAdmin }) => {
  const {
    currentIndex,
    isDragging,
    fileInputRef,
    goToPrev,
    goToNext,
    goToImage,
    handleUpload,
    handleDelete,
    setAsMainImage,
    handleDragOver,
    handleDragLeave,
    handleDrop,
  } = useGallery(images, setImages);

  return (
    <div className="gallery">
      <div className="gallery-main">
        <button
          type="button"
          className="slick-arrow prev-arrow"
          onClick={goToPrev}
          aria-label="Previous image"
        >
          <i className="fi fi-rr-arrow-left" />
        </button>

        {isAdmin ? (
          <div
            className={`gallery-image-wrapper${isDragging ? ' dragging' : ''}`}
            tabIndex={0}
            onClick={() => fileInputRef.current?.click()}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                fileInputRef.current?.click();
              }
            }}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            aria-label="Upload image by clicking or dragging and dropping a file"
          >
            {images[currentIndex]?.imageUrlSmall ? (
              <img
                src={images?.[currentIndex]?.imageUrlSmall} // fallback
                srcSet={`
                  ${images?.[currentIndex]?.imageUrlSmall} 480w,
                  ${images?.[currentIndex]?.imageUrlMedium} 768w,
                  ${images?.[currentIndex]?.imageUrlLarge} 1200w
                `}
                sizes="(max-width: 480px) 480px, (max-width: 768px) 768px, 1200px"
                alt={`Slide ${currentIndex + 1}`}
                className="gallery-image"
              />

            ) : (
              <span className="gallery-placeholder">Click or drag image to add</span>
            )}

            {isAdmin && images.length > 0 && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete();
                }}
                className="gallery-delete-button"
                title="Delete image"
              >
                ×
              </button>
            )}

            <input
              type="file"
              ref={fileInputRef}
              accept="image/*"
              onChange={handleUpload}
              style={{ display: 'none' }}
            />
          </div>
        ) : (
          <img
            src={images?.[currentIndex]?.imageUrlSmall} // fallback
            srcSet={`
              ${images?.[currentIndex]?.imageUrlSmall} 480w,
              ${images?.[currentIndex]?.imageUrlMedium} 768w,
              ${images?.[currentIndex]?.imageUrlLarge} 1200w
            `}
            sizes="(max-width: 480px) 480px, (max-width: 768px) 768px, 1200px"
            alt={`Slide ${currentIndex + 1}`}
            className="main-thumbnail-image"
          />

        )}

        <button
          type="button"
          className="slick-arrow next-arrow"
          onClick={goToNext}
          aria-label="Next image"
        >
          <i className="fi fi-rr-arrow-right" />
        </button>

        {isAdmin && (
          <div className="gallery-thumbnails">
            {images.map((img, idx) => (
              <div
                key={img.imageUrlSmall}
                className={`thumbnail-wrapper ${img.main ? 'main-thumbnail-image' : ''}`}
              >
                <button
                  type="button"
                  className="thumbnail-button"
                  onClick={() => goToImage(idx)}
                  aria-label={`View image thumbnail ${idx + 1}`}
                >
                  <img
                    src={img.imageUrlSmall} // fallback for very small or unsupported browsers
                    srcSet={`
                      ${img.imageUrlSmall} 480w,
                      ${img.imageUrlMedium} 768w,
                      ${img.imageUrlLarge} 1200w
                    `}
                    sizes="(max-width: 480px) 480px, (max-width: 768px) 768px, 1200px"
                    alt={`Thumbnail ${idx + 1}`}
                    className="thumbnail-image"
                  />

                </button>

                <button
                  className="set-main-button"
                  onClick={(e) => {
                    e.stopPropagation();
                    setAsMainImage(idx);
                  }}
                  title="Set as main image"
                >
                  {img.main ? '★' : '☆'}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <SlickDots
        count={images.length}
        currentIndex={currentIndex}
        onDotClick={goToImage}
        keys={images.map((img) => img.imageUrlSmall)}
      />
    </div>
  );
};

export default Gallery;