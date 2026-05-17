import { useCart } from '../../context/CartContext';
import { useCheckout } from '../../context/CheckoutContext';
import { trackAddPaymentInfo, trackPurchase, trackPurchaseFailed } from '../../utils/analytics';
import { PaymentProvider } from './useCreditCardForm';
import { useNavigate } from "react-router-dom";

export const usePayPal = () => {
  const { state, dispatch } = useCart();
  const navigate = useNavigate();
  const PAYPAL_CLIENT_ID = process.env.REACT_APP_PAYPAL_CLIENT_ID;
  const { billingData } = useCheckout();

  if (!PAYPAL_CLIENT_ID) {
    throw new Error('Missing REACT_APP_PAYPAL_CLIENT_ID environment variable');
  }

  const cartTotal = Number(
    state.items.reduce((sum, item) => sum + item.quantity * item.price, 0).toFixed(2)
  );

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
      const order = await actions.order.capture();

      const orderId = order.id;

      dispatch({ type: 'CLEAR_CART', payload: { reason: 'purchase' } });

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

      navigate('/success', {
        state: {
          orderNumber: orderId,
          email: billingData.email,
          total: cartTotal,
          paymentMethod: 'PayPal',
          fromCheckout: true,
        },
      });

    } catch (error: any) {
      trackPurchaseFailed(
        PaymentProvider.PAYPAL,
        cartTotal,
        error?.message || 'capture_failed'
      );
    }
  };


  return {
    PAYPAL_CLIENT_ID,
    cartTotal,
    createOrder,
    onApprove,
  };
};
