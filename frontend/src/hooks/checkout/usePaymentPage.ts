import { useState } from 'react';
import { useCheckoutSummary } from './useCheckoutSummary';

export const usePaymentPage = () => {
  const { enrichedItems, subtotalValue, shippingCost, total } = useCheckoutSummary();

  const [paymentMethod, setPaymentMethod] = useState<'creditCard' | 'paypal'>('creditCard');

  return {
    paymentMethod,
    setPaymentMethod,
    enrichedItems,
    subtotalValue,
    shippingCost,
    total,
  };
};
