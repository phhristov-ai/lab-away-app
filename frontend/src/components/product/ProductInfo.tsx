import React, { useState } from 'react';
import AddToCartButton from './AddToCartButton';
import './ProductInfo.css';
import QuantitySelector from './QuantitySelector';
import { useTranslation } from 'react-i18next';
import { useProductInfo } from '../../hooks/product/useProductInfo';
import { useNavigate } from 'react-router-dom';
import { Category } from '../../services/category/categoriesService';

type ProductInfoProps = {
  title: string;
  price: number;
  image: string;
  slug: string;
  categories: Category[];
  onTitleChange?: (newTitle: string) => void;
  onPriceChange?: (newPrice: number) => void;
  enabled: boolean;
};

const ProductInfo: React.FC<ProductInfoProps> = ({
  title,
  price,
  image,
  slug,
  categories,
  onTitleChange,
  onPriceChange,
  enabled
}) => {
  const { renderTitle, renderPrice } = useProductInfo({
    title,
    price,
    onTitleChange,
    onPriceChange,
  });

  const [quantity, setQuantity] = useState(1);
  const { t } = useTranslation();
  const navigate = useNavigate();
  const handleAddToCart = () => {
    navigate('/cart');
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
          categories={categories}
          enabled={enabled}
        />
      </div>
    </div>
  );
};

export default ProductInfo;