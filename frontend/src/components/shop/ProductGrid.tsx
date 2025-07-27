import React from 'react';
import './ProductGrid.css';
import { ProductPreviewType } from '../../types/ProductPreviewType';
import Product from '../homepage/Product';
import { useAdmin } from '../../context/AdminContext';
import { Link } from 'react-router-dom';

type ProductGridProps = {
  products: ProductPreviewType[];
  showCreateNew?: boolean;
};

const ProductGrid: React.FC<ProductGridProps> = ({ products, showCreateNew = false }) => {
    const { isAdmin } = useAdmin();

  return (
    <div className="product-grid">
      {isAdmin && showCreateNew && (
        <div className="product-item new-product-item">
          <Link to="/product/new" className="new-product-link">
            <div className="new-product-content">
              <div className="new-product-image-placeholder">+</div>
              <h2>Create New Product</h2>
              <p>Click here to add a new product.</p>
            </div>
          </Link>
        </div>
      )}

      {products.map((product) => (
        <div key={product.slug} className="product-item">
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