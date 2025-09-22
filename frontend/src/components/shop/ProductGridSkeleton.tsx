import React from 'react';
import './ProductGridSkeleton.css';

const ProductGridSkeleton: React.FC = () => {
  return (
    <div className="product-grid">
      {Array.from({ length: 8 }).map((_, index) => (
        <div key={index} className="product-item">
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
