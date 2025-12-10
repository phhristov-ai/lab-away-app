import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCheckoutSummary } from './useCheckoutSummary';

export const usePaymentPage = () => {
  const navigate = useNavigate();
  const { enrichedItems, subtotalValue, shippingCost, total } = useCheckoutSummary();

  const [paymentMethod, setPaymentMethod] = useState<'creditCard' | 'paypal'>('creditCard');

/*  useEffect(() => {
    if (enrichedItems.length === 0) {
      navigate('/');
    }
  }, [enrichedItems, navigate]);

  */

  return {
    paymentMethod,
    setPaymentMethod,
    enrichedItems,
    subtotalValue,
    shippingCost,
    total,
  };
};
