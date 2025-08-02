import React, { useState } from 'react';
import AddToCartButton from './AddToCartButton';
import './ProductInfo.css';
import QuantitySelector from './QuantitySelector';
import { useTranslation } from 'react-i18next';
import '../../pages/BlogPostPage.css';
import { useProductInfo } from '../../hooks/useProductInfo';

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
  const { renderTitle, renderPrice } = useProductInfo({
    title,
    price,
    onTitleChange,
    onPriceChange,
  });

  const [quantity, setQuantity] = useState(1);
  const { t } = useTranslation();

  const handleAddToCart = () => {
    console.log('Item added to cart');
  };

  return (
    <div className="product-info-inner">
      <div className="post-main">{renderTitle()}</div>
      <div className="product-price">{renderPrice()}</div>
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
          variant="secondary"
        />
      </div>
    </div>
  );
};

export default ProductInfo;