import React from 'react';
import './Product.css';
import ProductDetails from './ProductDetails';
import { Link } from 'react-router-dom';
import { ProductPreviewType } from '../../types/ProductPreviewType';

const Product: React.FC<ProductPreviewType> = ({ name, price, thumbnailUrl, slug, categories, onClick }) => {
  const product = { name, price, thumbnailUrl, slug, categories };

  return (
    <div className="product">
      <Link to={`/product/${slug}`} state={{ product }} onClick={() => onClick?.()}  className="product-link">
        <div className="product-image-wrapper">
          <img src={thumbnailUrl} alt={name} className="shop-product-image" />
        </div>
      </Link>
      <ProductDetails name={name} price={price} image={thumbnailUrl} slug={slug} categories={categories}/>
    </div>
  );
};


export default Product;
