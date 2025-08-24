import { useCart } from '../context/CartContext';

export const usePayPal = () => {
  const { state } = useCart();

  const PAYPAL_CLIENT_ID = process.env.REACT_APP_PAYPAL_CLIENT_ID;

  if (!PAYPAL_CLIENT_ID) {
    throw new Error('Missing REACT_APP_PAYPAL_CLIENT_ID environment variable');
  }

  const cartTotal = state.items
    .reduce((sum, item) => sum + item.quantity * item.price, 0)
    .toFixed(2);

  const getPayerName = (details: any): string =>
    details.payment_source?.paypal?.name?.given_name ?? 'Customer';

  const createOrder = (data: any, actions: any) => {
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
    if (actions.order) {
      const details = await actions.order.capture();
      const payerName = getPayerName(details);
      alert(`Transaction completed by ${payerName}`);
    }
  };

  return {
    PAYPAL_CLIENT_ID,
    cartTotal,
    createOrder,
    onApprove,
  };
};
