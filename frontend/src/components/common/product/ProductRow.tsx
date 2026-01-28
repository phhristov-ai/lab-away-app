import React from 'react';
import Product from './Product';
import './ProductRow.css';
import { ProductPreviewType } from '../../../types/ProductPreviewType';


type ProductRowProps = {
  products: ProductPreviewType[];
};

const ProductRow: React.FC<ProductRowProps> = ({ products }) => {
  return (
    <div className="product-row">
      {products.map((product) => (
        <Product
          key={product.slug}
          name={product.name}
          price={product.price}
          images={product.images}
          slug={product.slug}
          categories={product.categories}
          active={product.active}
        />
      ))}
    </div>
  );
};

export default ProductRow;
