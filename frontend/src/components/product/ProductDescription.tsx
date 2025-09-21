import React from 'react';
import './ProductDescription.css';

type ProductDescriptionProps = {
  description: string;
  isAdmin?: boolean;
  onDescriptionChange?: (newDescription: string) => void;
};

const ProductDescription: React.FC<ProductDescriptionProps> = ({
  description,
  isAdmin = false,
  onDescriptionChange,
}) => {
  if (isAdmin) {
    return (
      <textarea
        className="product-description-input"
        value={description}
        onChange={(e) => onDescriptionChange?.(e.target.value)}
        rows={6}
        placeholder="Enter product description with optional HTML"
      />
    );
  }

  return (
    <div
      className="product-long-desc"
      dangerouslySetInnerHTML={{ __html: description }}
    />
  );
};

export default ProductDescription;
