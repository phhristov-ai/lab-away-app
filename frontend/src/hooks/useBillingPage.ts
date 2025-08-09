import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCheckout } from '../context/CheckoutContext';
import { BillingFormHandle } from '../components/checkout/billing/BillingForm';
import { ShippingFormHandle } from '../components/checkout/billing/ShippingForm';

export function useBillingPage() {
  const { billingData, setBillingData, setShippingData } = useCheckout();
  const [showShipping, setShowShipping] = useState(false);
  const navigate = useNavigate();

  const billingFormRef = useRef<BillingFormHandle>(null);
  const shippingFormRef = useRef<ShippingFormHandle>(null);

  const handleCheckboxChange = (checked: boolean) => {
    setShowShipping(checked);
  };

  const handleNextClick = () => {
    const isBillingValid = billingFormRef.current?.validate() ?? false;
    const isShippingValid = !showShipping || (shippingFormRef.current?.validate() ?? false);

    if (!isBillingValid || !isShippingValid) return;

    if (!showShipping) {
      setShippingData(billingData);
    }

    navigate('/payment');
  };

  return {
    billingData,
    setBillingData,
    setShippingData,
    showShipping,
    handleCheckboxChange,
    handleNextClick,
    billingFormRef,
    shippingFormRef,
  };
}
