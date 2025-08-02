import React, { useState } from 'react';
import AddToCartButton from './AddToCartButton';
import './ProductInfo.css';
import QuantitySelector from './QuantitySelector';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../utils/format';
import { useAdmin } from '../../context/AdminContext';
import '../../pages/BlogPostPage.css';

type ProductInfoProps = {
  title: string;
  price: number;
  image: string;
  slug: string;
  onTitleChange?: (newTitle: string) => void;
  onPriceChange?: (newPrice: number) => void;
};

const ProductInfo: React.FC<ProductInfoProps> = ({
  title,
  price,
  image,
  slug,
  onTitleChange,
  onPriceChange,
}) => {
  const { isAdmin } = useAdmin();
  const [quantity, setQuantity] = useState(1);
  const { t, i18n } = useTranslation();

  const handleAddToCart = () => {
    console.log('Item added to cart');
  };

  const renderTitle = () => {
    if (isAdmin) {
      return (
        <input
          type="text"
          placeholder="Enter title"
          value={title}
          onChange={(e) => onTitleChange?.(e.target.value)}
          className="post-input"
        />
      );
    }
    if (title) {
      return <h1>{title}</h1>;
    }
    return <div className="title-placeholder" />;
  };

  const renderPrice = () => {
    if (isAdmin) {
      return (
        <input
          type="number"
          placeholder="Enter price"
          value={price}
          onChange={(e) => {
            const value = parseFloat(e.target.value);
            if (!isNaN(value)) onPriceChange?.(value);
          }}
          className="post-input"
        />
      );
    }

    if (price > 0) {
      return <p className="price-text">{formatCurrency(price, i18n.language)}</p>;
    }

    return <div className="price-placeholder" />;
  };

  return (
    <div className="product-info-inner">
      <div className="post-main">
        {renderTitle()}
      </div>

      <div className="product-price">
        {renderPrice()}
      </div>

      <div className="product-actions">
        <QuantitySelector value={quantity} onChange={setQuantity} />

        <AddToCartButton
          onClick={handleAddToCart}
          label={t('productPage.buttons.addToCart')}
          name={title}
          price={price}
          image={image}
          quantity={quantity}
          slug={slug}
          variant='secondary'
        />
      </div>
    </div>
  );
};

export default ProductInfo;