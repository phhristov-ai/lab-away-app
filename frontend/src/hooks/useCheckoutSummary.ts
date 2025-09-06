import { useTranslation } from 'react-i18next';
import { useCart } from '../context/CartContext';

export const useCheckoutSummary = () => {
  const { state } = useCart();
  const { t } = useTranslation();

  const enrichedItems = state.items.map(item => ({
    ...item,
    subTitle: t('checkout.cart.columns.immediateResults'),
    subtotal: item.price * item.quantity,
    title: item.name,
    product: item.name,
  }));

  const subtotalValue = state.items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const shippingCost = 0;
  const total = subtotalValue + shippingCost;

  return {
    enrichedItems,
    subtotalValue,
    shippingCost,
    total,
  };
};
