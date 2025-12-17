import React from 'react';
import './OrderSummary.css';
import { useCart } from '../../../context/CartContext';
import OrderItemRow from './OrderItemRow';
import ShippingDetails from './ShippingDetails';
import SummaryTotals from './SummaryTotals';
import { useTranslation } from 'react-i18next';
import CustomButton from '../shipping/CustomButton';
import { Category } from '../../../services/category/categoriesService';
import { CartItem } from '../../../types/CartItem';

type ShippingInfo = {
  name: string;
  address: string;
  phone: string;
  city: string;
  countryName: string;
  countryCode: string;
  email: string;
};

type Props = {
  items: CartItem[];
  shipping: ShippingInfo;
  subtotal: number;
  shippingCost: number;
  total: number;
  showNextButton?: boolean;
  onNextClick?: () => void;
};

const OrderSummary: React.FC<Props> = ({
  items, shipping, subtotal, shippingCost, total, showNextButton, onNextClick
}) => {
  const { dispatch } = useCart();
  const { t } = useTranslation();
  const handleQuantityChange = (slug: string, quantity: number, categories: Category[]) => {
    dispatch({ type: 'UPDATE_QUANTITY', payload: { slug, quantity, categories } });
  };

  return (
    <div className="order-summary">
      <h2 className="section-title">
        {t('checkout.summary.title')}
      </h2>

      <table className="order-summary-table">
        <tbody>
          {items.map((item) => (
            <OrderItemRow
              key={item.slug}
              item={item}
              onQuantityChange={handleQuantityChange}
            />
          ))}
          <ShippingDetails {...shipping} />
          <SummaryTotals subtotal={subtotal} shippingCost={shippingCost} total={total} />
        </tbody>
      </table>

      {showNextButton && (
        <div className="align-right" style={{ marginTop: '1rem' }}>
          <CustomButton
            label={t('checkout.actions.next')}
            onClick={onNextClick}
            variant="primary"
            fullWidth={true}
          />
        </div>
      )}
    </div>
  );
};

export default OrderSummary;
