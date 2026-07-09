import { useTranslation } from 'react-i18next';
import { useCart } from '../../context/CartContext';
import { useCheckout } from '../../context/CheckoutContext';
import { trackAddPaymentInfo, trackPurchase, trackPurchaseFailed } from '../../utils/analytics';
import { useCreateOrder } from './useCreateOrder';
import { PaymentProvider } from './useCreditCardForm';
import { useNavigate } from "react-router-dom";
import { useRef } from 'react';

  function splitFullName(fullName = '') {
    const parts = fullName.trim().split(/\s+/);
    const firstName = parts[0] || '';
    const lastName = parts.length > 1 ? parts.slice(1).join(' ') : '';
    return { firstName, lastName };
  }

export const usePayPal = () => {
  const { state, dispatch } = useCart();
  const navigate = useNavigate();
  const PAYPAL_CLIENT_ID = process.env.REACT_APP_PAYPAL_CLIENT_ID;
  const { billingData } = useCheckout();
  const { t, i18n } = useTranslation();

  if (!PAYPAL_CLIENT_ID) {
    throw new Error('Missing REACT_APP_PAYPAL_CLIENT_ID environment variable');
  }

  const cartTotal = Number(
    state.items.reduce((sum, item) => sum + item.quantity * item.price, 0).toFixed(2)
  );

  const { createOrder: createBackendOrder } = useCreateOrder();

  const backendOrderRef = useRef<any>(null);

  const buildOrderData = () => ({
      customerEmail: billingData.email,
      paymentProvider: PaymentProvider.PAYPAL,
      billingAddress: {
        ...splitFullName(billingData.fullName),
        address: billingData.address,
        city: billingData.city,
        country: billingData.countryCode,
        postCode: billingData.postcode,
        phone: billingData.phone,
      },
      shippingAddress: {
        ...splitFullName(billingData.fullName),
        address: billingData.address,
        city: billingData.city,
        country: billingData.countryCode,
        postCode: billingData.postcode,
        phone: billingData.phone,
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

  const createOrder = async (data: any, actions: any) => {
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

    backendOrderRef.current = await createBackendOrder(buildOrderData());

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
      const gaClientId = getGAClientId();

      await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders/confirm`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          orderNumber: backendOrderRef.current!.orderNumber,
          gaClientId,
        }),
      });

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
