import React from 'react';
import { Link } from 'react-router-dom';
import ProductDetails from './ProductDetails';
import './Product.css';
import { ProductPreviewType } from '../../../types/ProductPreviewType';

const Product: React.FC<ProductPreviewType> = ({ name, price, images, slug, categories, onClick, active }) => {
  const product = { name, price, images, slug, categories, active };

  const isDisabled = !active;
  console.log(images);
  return (
    <div className={`product ${isDisabled ? 'inactive' : ''}`}>
      <Link
        to={`/product/${slug}`}
        state={{ product }}
        onClick={() => onClick?.()}
        className="product-link"
      >
        <div className="product-image-wrapper">
          <picture>
            <source media="(min-width: 1200px)" srcSet={images?.[0]?.imageUrlLarge} />
            <source media="(min-width: 768px)" srcSet={images?.[0]?.imageUrlMedium} />
            <img
              src={images?.[0]?.imageUrlSmall}
              alt={name}
              className="shop-product-image"
              loading="lazy"
            />
          </picture>

          {!active && (
            <div className="out-of-stock-overlay">Out of stock</div>
          )}
        </div>

      </Link>
      <ProductDetails
        name={name}
        price={price}
        image={images?.[0]?.imageUrlSmall}
        slug={slug}
        categories={categories}
        active={active}
      />
    </div>
  );
};

export default Product;
