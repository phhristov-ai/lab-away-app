import { useCart } from '../../context/CartContext';

export const useCheckoutSummary = () => {
  const { state } = useCart();

  const enrichedItems = state.items.map(item => ({
    ...item,
    subtotal: item.price * item.quantity,
    title: item.name,
    product: item.name,
    categories: item.categories
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
