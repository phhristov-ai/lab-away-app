import React from 'react';
import './ProductGridSkeleton.css';

const ProductGridSkeleton: React.FC = () => {
  const skeletonCount = 8;

  return (
    <div className="product-grid">
      {Array.from({ length: skeletonCount }, (_, i) => (
        <div key={`skeleton-${i}`} className="product-item">
          <div className="product-skeleton-card">
            <div className="skeleton-image" />
            <div className="skeleton-text short" />
            <div className="skeleton-text long" />
          </div>
        </div>
      ))}
    </div>
  );
};

export default ProductGridSkeleton;
