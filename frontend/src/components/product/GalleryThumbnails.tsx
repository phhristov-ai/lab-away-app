import React, { useCallback } from 'react';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  SortableContext,
  useSortable,
  arrayMove,
  rectSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

type Props = {
  images: ProductImage[];
  currentIndex: number;
  goToImage: (index: number) => void;
  setImages: React.Dispatch<React.SetStateAction<ProductImage[]>>;
};

const SortableThumbnail: React.FC<{
  img: ProductImage;
  idx: number;
  isActive: boolean;
  onClick: () => void;
}> = ({ img, idx, isActive, onClick }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: img.id,
  });

  const style: React.CSSProperties = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
    cursor: 'grab',
    touchAction: 'none', // ✅ CRITICAL FIX
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`thumbnail-wrapper ${isActive ? 'active-thumbnail' : ''}`}
    >
      {/* Drag handle (prevents button blocking drag) */}
      <div {...attributes} {...listeners} className="drag-handle">
        ⠿
      </div>

      {/* Clickable image (NO drag listeners here) */}
      <button
        type="button"
        className="thumbnail-button"
        onClick={onClick}
        aria-label={`View image thumbnail ${idx + 1}`}
      >
        <img
          src={img.imageUrlSmall}
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
    </div>
  );
};

const GalleryThumbnails: React.FC<Props> = ({
  images,
  currentIndex,
  goToImage,
  setImages,
}) => {
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 5, // prevents accidental drag on click
      },
    })
  );

  const handleDragEnd = useCallback(
    (event: any) => {
      const { active, over } = event;

      if (!over || active.id === over.id) return;

      setImages((prev) => {
        const oldIndex = prev.findIndex((i) => i.id === active.id);
        const newIndex = prev.findIndex((i) => i.id === over.id);

        const reordered = arrayMove(prev, oldIndex, newIndex);

        return reordered.map((img, index) => ({
          ...img,
          order: index,
        }));
      });
    },
    [setImages]
  );

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragEnd={handleDragEnd}
    >
      <SortableContext
        items={images.map((img) => img.id)}
        strategy={rectSortingStrategy}
      >
        <div className="gallery-thumbnails">
          {images.map((img, idx) => {

            return (
              <SortableThumbnail
                key={img.id}
                img={img}
                idx={idx}
                isActive={idx === currentIndex}
                onClick={() => goToImage(idx)}
              />
            );
          })}
        </div>
      </SortableContext>
    </DndContext>
  );
};

export default GalleryThumbnails;