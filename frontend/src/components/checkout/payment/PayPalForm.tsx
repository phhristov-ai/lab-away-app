import { PayPalScriptProvider, PayPalButtons } from '@paypal/react-paypal-js';
import { usePayPal } from '../../../hooks/checkout/usePayPal';

const PayPalForm: React.FC = () => {
  const { PAYPAL_CLIENT_ID, createOrder, onApprove } = usePayPal();

  return (
    <PayPalScriptProvider options={{ clientId: PAYPAL_CLIENT_ID, currency: 'EUR' }}>
      <PayPalButtons
        style={{ layout: 'vertical', height: 45 }}
        createOrder={createOrder}
        onApprove={(_, actions) => onApprove(actions)}
      />
    </PayPalScriptProvider>
  );
};

export default PayPalForm;