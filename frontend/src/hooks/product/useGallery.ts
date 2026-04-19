import { useState, useRef, useEffect, useCallback } from 'react';

export function useGallery(images: ProductImage[], setImages: React.Dispatch<React.SetStateAction<ProductImage[]>>) {
  const [currentIndex, setCurrentIndex] = useState(0);

  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (images.length === 0) {
      setCurrentIndex(0);
      return;
    }

    setCurrentIndex((prev) => {
      if (prev >= images.length) return images.length - 1;
      return prev;
    });
  }, [images, currentIndex]);

  const goToPrev = useCallback(() => {
    setCurrentIndex((prev) => {
      const len = images.length;
      if (len === 0) return 0;
      return prev === 0 ? len - 1 : prev - 1;
    });
  }, [images.length]);

  const goToNext = useCallback(() => {
    setCurrentIndex((prev) => {
      const len = images.length;
      if (len === 0) return 0;
      return prev === len - 1 ? 0 : prev + 1;
    });
  }, [images.length]);

  const goToImage = useCallback((index: number) => {
    setCurrentIndex(index);
  }, []);

  const handleUpload = (
    event: React.ChangeEvent<HTMLInputElement> | React.DragEvent<HTMLDivElement>
  ) => {
    const files =
      'dataTransfer' in event ? event.dataTransfer.files : event.target.files;

    if (!files || files.length === 0) return;

    const newImages: ProductImage[] = Array.from(files).map((file) => {
      const url = URL.createObjectURL(file);

      return {
        id: crypto.randomUUID(),
        imageUrlSmall: url,
        imageUrlMedium: url,
        imageUrlLarge: url,
        file,
      };
    });

    setImages((prev) => {
      const updated = [...prev, ...newImages];
      return updated;
    });
  };

  useEffect(() => {
  if (images.length > 0) {
    setCurrentIndex(images.length - 1);
  }
}, [images.length]);

  const handleDelete = useCallback(() => {
    setImages((prevImages) => {
      if (prevImages.length === 0) return prevImages;

      const updated = prevImages.filter((_, index) => index !== currentIndex);

      setCurrentIndex((prev) => Math.max(0, Math.min(prev, updated.length - 1)));

      return updated;
    });
  }, [currentIndex]);

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
    globalThis.addEventListener('keydown', handleKeyDown);
    return () => globalThis.removeEventListener('keydown', handleKeyDown);
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
    handleDragOver,
    handleDragLeave,
    handleDrop,
  };
}
