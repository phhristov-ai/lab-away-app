import { useStripe } from "@stripe/react-stripe-js";
import { useCreateOrder } from "./useCreateOrder";
import { useCheckout } from "../context/CheckoutContext";
import { useStripePayment } from "./useStripePayment";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useTranslation } from "react-i18next";
import i18n from "../i18n/i18n";
import { cardStyle } from "../types/stripeStyles";

export enum PaymentProvider {
  STRIPE = 'STRIPE',
  PAYPAL = 'PAYPAL',
}

export const useCreditCardForm = () => {
  const stripe = useStripe();
  const { createOrder } = useCreateOrder();
  const { billingData, shippingData } = useCheckout();
  const { confirmPayment } = useStripePayment();
  const navigate = useNavigate();
  const { state, dispatch } = useCart();
  const { t } = useTranslation();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const orderData = {
        billingEmail: billingData.email,
        billingPhone: billingData.phone,
        paymentProvider: PaymentProvider.STRIPE,
        billingAddress: {
          firstName: billingData.firstName,
          lastName: billingData.lastName,
          address: billingData.address,
          city: billingData.city,
          country: billingData.country,
          postCode: billingData.postcode,
        },
        shippingAddress: {
          firstName: shippingData.firstName,
          lastName: shippingData.lastName,
          address: shippingData.address,
          city: shippingData.city,
          country: shippingData.country,
          postCode: shippingData.postcode,
        },
        items: state.items.map(item => ({
          slug: item.slug,
          quantity: item.quantity,
          price: item.price,
        })),
        language: i18n.language,
      };

      // 2. Create order
      const order = await createOrder(orderData);
      console.log('✅ Order created:', order);

      console.log('🔐 clientSecret:', order.clientSecret);

      // 4. Confirm payment with Stripe
      const result = await confirmPayment(order.clientSecret, billingData);

      if (result.error) {
        console.error('[❌ Payment Error]', result.error.message);
        return;
      }

      if (result.paymentIntent?.status === 'succeeded') {
        console.log('✅ Payment successful');

        // 5. Optionally confirm the order in backend
        await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders/confirm/${order.orderNumber}`, {
          method: 'POST',
        });

        dispatch({ type: 'CLEAR_CART' });

        navigate('/success', {
          state: {
            orderNumber: order.orderNumber,
            email: billingData.email,
            total: order.total,
            paymentMethod: 'Cash on delivery',
            shippingAddress: shippingData,
          },
        });
      }
    } catch (error) {
      console.error('❌ Error during checkout:', error);
    }
  };
  
  return {
    stripe,
    t,
    handleSubmit,
    cardStyle,
  };
};