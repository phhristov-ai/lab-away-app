import React, { useCallback, useEffect, useState, ChangeEvent, DragEvent, useRef } from 'react'; import './Gallery.css';
import SlickDots from './SlickDots';
import { ProductImage } from '../../types/ProductImage';
import './Gallery.css';

interface GalleryProps {
  images: ProductImage[];
  setImages: React.Dispatch<React.SetStateAction<ProductImage[]>>;
  isAdmin: boolean;
}

const Gallery: React.FC<GalleryProps> = ({ images, setImages, isAdmin }) => {
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [isDragging, setIsDragging] = useState(false);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const goToPrev = useCallback(() => {
    setCurrentIndex(prev => (prev === 0 ? images.length - 1 : prev - 1));
  }, [images.length]);

  const goToNext = useCallback(() => {
    setCurrentIndex(prev => (prev === images.length - 1 ? 0 : prev + 1));
  }, [images.length]);

  const goToImage = useCallback((index: number) => {
    setCurrentIndex(index);
  }, []);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft') goToPrev();
      if (e.key === 'ArrowRight') goToNext();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [goToPrev, goToNext]);

  const handleUpload = (
    event: ChangeEvent<HTMLInputElement> | DragEvent<HTMLDivElement>
  ) => {
    const files = 'dataTransfer' in event ? event.dataTransfer.files : event.target.files;
    if (!files || files.length === 0) return;

    const file = files[0];
    const imageUrl = URL.createObjectURL(file);

    const newImage: ProductImage = {
      imageUrl,
      main: false,
      file,
    };

    setImages(prevImages => {
      const updatedImages = [...prevImages, newImage];
      setCurrentIndex(updatedImages.length - 1);
      return updatedImages;
    });
    setCurrentIndex(images.length);
  };


  const handleDragOver = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    handleUpload(e);
  };

  const handleDelete = () => {
    if (images.length === 0) return;

    const updatedImages = images.filter((_, index) => index !== currentIndex);
    setImages(updatedImages);

    // Adjust currentIndex
    if (currentIndex >= updatedImages.length) {
      setCurrentIndex(Math.max(0, updatedImages.length - 1));
    }
  };

  const setAsMainImage = (index: number) => {
    setImages((prevImages) =>
      prevImages.map((img, idx) => ({
        ...img,
        main: idx === index,
      }))
    );
    setCurrentIndex(index);
  };


  if (images.length === 0) return <div>No images to display.</div>;

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

            {/* ❌ Delete button */}
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
            className="main-image"
          />
        )}

        <i className="fi fi-rr-arrow-right slick-arrow next-arrow" onClick={goToNext} />

        <div className="gallery-thumbnails">
          {images.map((img, idx) => (
            <div key={img.imageUrl} className={`thumbnail-wrapper ${img.main ? 'main-image' : ''}`}>
              <img
                src={img.imageUrl}
                alt={`Thumbnail ${idx + 1}`}
                onClick={() => goToImage(idx)}
                className="thumbnail-image"
              />
              {isAdmin && (
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
              )}
            </div>
          ))}
        </div>

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