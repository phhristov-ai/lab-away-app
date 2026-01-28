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
  return (
    <div className="product-description">

      {isAdmin ? (
        <>
          <h3 className="product-description-title">Description</h3>
          <textarea
            className="product-description-input"
            value={description}
            onChange={(e) => onDescriptionChange?.(e.target.value)}
            rows={20}
            placeholder="Enter product description (HTML allowed)"
          />
          <span className="product-description-hint">
            You may use basic HTML tags (p, ul, li, strong, img)
          </span>
        </>
      ) : (
        <div
          className="product-long-desc"
          dangerouslySetInnerHTML={{ __html: description }}
        />
      )}
    </div>
  );
};

export default ProductDescription;
