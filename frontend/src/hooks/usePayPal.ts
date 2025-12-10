import { useCart } from '../context/CartContext';
import { trackAddPaymentInfo, trackPurchase, trackPurchaseFailed } from '../utils/analytics';
import { PaymentProvider } from './useCreditCardForm';

export const usePayPal = () => {
  const { state } = useCart();

  const PAYPAL_CLIENT_ID = process.env.REACT_APP_PAYPAL_CLIENT_ID;

  if (!PAYPAL_CLIENT_ID) {
    throw new Error('Missing REACT_APP_PAYPAL_CLIENT_ID environment variable');
  }

  const cartTotal = Number(
    state.items.reduce((sum, item) => sum + item.quantity * item.price, 0).toFixed(2)
  );

  const getPayerName = (details: any): string =>
    details.payment_source?.paypal?.name?.given_name ?? 'Customer';

  const createOrder = (data: any, actions: any) => {

    trackAddPaymentInfo(
      state.items.map(item => ({
        item_id: item.slug,
        item_name: item.name,
        price: item.price,
        quantity: item.quantity,
        item_category: item.categories?.[0]?.name,
        item_category2: item.categories?.[1]?.name,
      })),
      PaymentProvider.PAYPAL
    );

    return actions.order.create({
      intent: 'CAPTURE',
      purchase_units: [
        {
          amount: {
            currency_code: 'EUR',
            value: cartTotal,
          },
        },
      ],
    });
  };

  const onApprove = async (actions: any) => {
    try {
      const details = await actions.order.capture();
      const payerName = getPayerName(details);
      const orderId = details.id || details.purchase_units?.[0]?.reference_id || 'unknown';

      trackPurchase(
        orderId,
        state.items.map(item => ({
          item_id: item.slug,
          item_name: item.name,
          price: item.price,
          quantity: item.quantity,
          item_category: item.categories?.[0]?.name,
          item_category2: item.categories?.[1]?.name,
        })),
        cartTotal
      );

      alert(`Transaction completed by ${payerName}`);
    } catch (error: any) {
      // ❌ Failed payment
      trackPurchaseFailed(
        PaymentProvider.PAYPAL,
        cartTotal,
        error?.message || 'capture_failed'
      );
    };
  }

  return {
    PAYPAL_CLIENT_ID,
    cartTotal,
    createOrder,
    onApprove,
  };
};
