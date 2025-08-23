import { useState, useRef, useEffect, useCallback } from 'react';
import { ProductImage } from '../types/ProductImage';

export function useGallery(images: ProductImage[], setImages: React.Dispatch<React.SetStateAction<ProductImage[]>>) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const goToPrev = useCallback(() => {
    setCurrentIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  }, [images.length]);

  const goToNext = useCallback(() => {
    setCurrentIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
  }, [images.length]);

  const goToImage = useCallback((index: number) => {
    setCurrentIndex(index);
  }, []);

  const handleUpload = (
    event: React.ChangeEvent<HTMLInputElement> | React.DragEvent<HTMLDivElement>
  ) => {
    const files = 'dataTransfer' in event ? event.dataTransfer.files : event.target.files;
    if (!files || files.length === 0) return;

    const newImages: ProductImage[] = Array.from(files).map((file) => ({
      imageUrl: URL.createObjectURL(file),
      main: false,
      file,
    }));

    setImages((prevImages) => {
      const updatedImages = [...prevImages, ...newImages];
      return updatedImages;
    });

  };

  const handleDelete = () => {
    if (images.length === 0) return;
    const updatedImages = images.filter((_, index) => index !== currentIndex);
    setImages(updatedImages);

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

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    handleUpload(e);
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft') goToPrev();
      if (e.key === 'ArrowRight') goToNext();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [goToPrev, goToNext]);

  return {
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
  };
}
