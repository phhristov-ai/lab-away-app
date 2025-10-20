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

function splitFullName(fullName = '') {
  const parts = fullName.trim().split(/\s+/);
  const firstName = parts[0] || '';
  const lastName = parts.length > 1 ? parts.slice(1).join(' ') : '';
  return { firstName, lastName };
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
        customerEmail: shippingData.email,
        paymentProvider: PaymentProvider.STRIPE,
        billingAddress: {
          ...splitFullName(billingData.fullName),
          address: billingData.address,
          city: billingData.city,
          country: billingData.countryCode,
          postCode: billingData.postcode,
          phone: billingData.phone
        },
        shippingAddress: {
          ...splitFullName(shippingData.fullName),
          address: shippingData.address,
          city: shippingData.city,
          country: shippingData.countryCode,
          postCode: shippingData.postcode,
          phone: shippingData.phone,

        },
        items: state.items.map(item => ({
          slug: item.slug,
          quantity: item.quantity,
          price: item.price,
        })),
        language: i18n.language,
      };

      const order = await createOrder(orderData);
      const result = await confirmPayment(order.clientSecret, billingData);

      if (result.error) {
        console.error('[❌ Payment Error]', result.error.message);
        return;
      }

      if (result.paymentIntent?.status === 'succeeded') {
        await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders/confirm/${order.orderNumber}`, {
          method: 'POST',
        });

        dispatch({ type: 'CLEAR_CART' });

        navigate('/success', {
          state: {
            orderNumber: order.orderNumber,
            email: billingData.email,
            total: order.total,
            paymentMethod: 'Credit Card',
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