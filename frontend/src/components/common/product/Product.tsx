import React from 'react';
import './Product.css';
import ProductDetails from './ProductDetails';
import { Link } from 'react-router-dom';
import { ProductPreviewType } from '../../../types/ProductPreviewType';

const Product: React.FC<ProductPreviewType> = ({ name, price, images, slug, categories, onClick }) => {
  const product = { name, price, images, slug, categories };


  return (
    <div className="product">
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
            <img src={images?.[0]?.imageUrlSmall} alt={name} className="shop-product-image" loading="lazy" />
          </picture>
        </div>
      </Link>
      <ProductDetails
        name={name}
        price={price}
        image={images?.[0]?.imageUrlSmall} 
        slug={slug}
        categories={categories}
      />
    </div>
  );
};

export default Product;
