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
        <i className="fi fi-rr-arrow-left slick-arrow prev-arrow" onClick={goToPrev} />

        {isAdmin ? (
          <div
            className={`gallery-image-wrapper${isDragging ? ' dragging' : ''}`}
            onClick={() => fileInputRef.current?.click()}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
          >
            {images[currentIndex]?.imageUrl ? (
              <img
                src={images[currentIndex].imageUrl}
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
            src={images[currentIndex].imageUrl}
            alt={`Slide ${currentIndex + 1}`}
            className="main-thumbnail-image"
          />
        )}

        <i className="fi fi-rr-arrow-right slick-arrow next-arrow" onClick={goToNext} />

        {isAdmin && (
          <div className="gallery-thumbnails">
            {images.map((img, idx) => (
              <div
                key={img.imageUrl}
                className={`thumbnail-wrapper ${img.main ? 'main-thumbnail-image' : ''}`}
              >
                <img
                  src={img.imageUrl}
                  alt={`Thumbnail ${idx + 1}`}
                  onClick={() => goToImage(idx)}
                  className="thumbnail-image"
                />
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
        keys={images.map((img) => img.imageUrl)}
      />
    </div>
  );
};

export default Gallery;