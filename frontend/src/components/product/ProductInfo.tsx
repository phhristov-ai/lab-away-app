import React from 'react';
import AddToCartButton from './AddToCartButton';
import './ProductInfo.css';
import QuantitySelector from './QuantitySelector';
import { useTranslation } from 'react-i18next';
import { useProductInfo } from '../../hooks/product/useProductInfo';
import { Category } from '../../services/category/categoriesService';
import { useAdmin } from '../../context/AdminContext';
import PayPalForm from '../checkout/payment/PayPalForm';
import { useAddToCart } from './useAddToCart';

type ProductInfoProps = {
  title: string;
  price: number;
  image: string;
  slug: string;
  categories: Category[];
  onTitleChange?: (newTitle: string) => void;
  onPriceChange?: (newPrice: number) => void;
  active: boolean | undefined;
  quantity: number;
  setQuantity: React.Dispatch<React.SetStateAction<number>>;
  handleAddToCart: () => void;
};

const ProductInfo: React.FC<ProductInfoProps> = ({
  title,
  price,
  image,
  slug,
  categories,
  onTitleChange,
  onPriceChange,
  active,
  handleAddToCart,
  quantity,
  setQuantity
}) => {
  const { renderTitle, renderPrice } = useProductInfo({
    title,
    price,
    onTitleChange,
    onPriceChange,
  });

  const { t } = useTranslation();
  const { isAdmin } = useAdmin();
  const addToCart = useAddToCart();

  return (
    <div className="product-info-inner">
      <div className="post-main">{renderTitle()}</div>
      <div className="product-price">{renderPrice()}</div>
      {!isAdmin && active === false && (
        <div className="out-of-stock-label">
          Out of stock
        </div>
      )}
      {!isAdmin &&
        <div className="product-actions">
          <QuantitySelector value={quantity} onChange={setQuantity} active={active} />
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
            active={active}
          />

        </div>
      }
      {!isAdmin &&
        <div className="paypal-wrapper">
          <PayPalForm
            onBeforePay={() => {
              addToCart({ slug, name: title, price, quantity, image, categories });
            }}
          />
        </div>
      }
    </div>
  );
};

export default ProductInfo;