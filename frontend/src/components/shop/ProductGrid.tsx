import React from 'react';
import './ProductGrid.css';
import { ProductPreviewType } from '../../types/ProductPreviewType';
import Product from '../homepage/Product';

type ProductGridProps = {
  products: ProductPreviewType[];
};

const ProductGrid: React.FC<ProductGridProps> = ({ products }) => {
  return (
    <div className="product-grid">
      {products.map((product) => (
        <div key={product.slug}>
          <Product
            name={product.name}
            price={product.price}
            thumbnailUrl={product.thumbnailUrl}
            slug={product.slug}
            categories={product.categories}
          />
        </div>
      ))}

    </div>
  );
};

export default ProductGrid;
