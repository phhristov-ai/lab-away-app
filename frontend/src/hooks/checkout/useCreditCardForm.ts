import { useStripe } from "@stripe/react-stripe-js";
import { useCreateOrder } from "./useCreateOrder";
import { useCheckout } from "../../context/CheckoutContext";
import { useStripePayment } from "./useStripePayment";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import { useTranslation } from "react-i18next";
import { cardStyle } from "../../types/stripeStyles";
import { trackAddPaymentInfo, trackGAEvent, trackPurchase } from "../../utils/analytics";
import { useState } from "react";

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
  const { t, i18n } = useTranslation();
  const [error, setError] = useState<string | null>(null);

  /** Utility: map cart items to GAItem format */
  const mapItemsToGA = (items: typeof state.items) =>
    items.map(item => ({
      item_id: item.slug,
      item_name: item.name,
      price: item.price,
      quantity: item.quantity,
      item_category: item.categories?.[0]?.name,
      item_category2: item.categories?.[1]?.name,
    }));

  const trackPaymentStep = () => {
    trackAddPaymentInfo(mapItemsToGA(state.items), PaymentProvider.STRIPE);
  };

  const trackFailure = (reason?: string, value?: number) => {
    trackGAEvent('purchase_failed', {
      method: PaymentProvider.STRIPE,
      reason: reason || 'unknown_error',
      value: value ?? state.items.reduce((sum, i) => sum + i.price * i.quantity, 0),
    });
  };

  const buildOrderData = () => ({
    customerEmail: shippingData.email,
    paymentProvider: PaymentProvider.STRIPE,
    billingAddress: {
      ...splitFullName(billingData.fullName),
      address: billingData.address,
      city: billingData.city,
      country: billingData.countryCode,
      postCode: billingData.postcode,
      phone: billingData.phone,
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
  });

  function getGAClientId(): string | null {
    let clientId: string | null = null;

    if (typeof window !== 'undefined' && (window as any).gtag) {
      const gaCookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('_ga='));

      if (gaCookie) {
        const parts = gaCookie.split('.');
        clientId = parts.slice(-2).join('.');
      }
    }

    return clientId;
  }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);
    trackPaymentStep();

    try {
      const orderData = buildOrderData();
      const order = await createOrder(orderData);
      const result = await confirmPayment(order.clientSecret, billingData);

      if (result.error) {
        setError(result.error.message || 'Payment failed.');
        trackFailure(result.error.message || 'Payment failed.', order.total);
        return;
      }

      if (result.paymentIntent?.status === 'succeeded') {
        const gaClientId = getGAClientId();

        await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders/confirm`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            orderNumber: order.orderNumber,
            gaClientId,
          }),
        });

        dispatch({ type: 'CLEAR_CART', payload: { reason: 'purchase' } });

        trackPurchase(
          order.orderNumber,
          state.items.map(item => ({
            item_id: item.slug,
            item_name: item.name,
            price: item.price,
            quantity: item.quantity,
            item_category: item.categories?.[0]?.name,
            item_category2: item.categories?.[1]?.name,
          })),
          order.total
        );

        navigate('/success', {
          state: {
            orderNumber: order.orderNumber,
            email: billingData.email,
            total: order.total,
            paymentMethod: 'Credit Card',
            shippingAddress: shippingData,
            fromCheckout: true
          },
        });
      }
    } catch (error: any) {
      console.error('❌ Error during checkout:', error);
      setError(error?.message || t('checkout.payment.creditCard.genericError'));
      trackFailure(error?.message);
    }
  };

  return { stripe, t, handleSubmit, cardStyle, error };
};