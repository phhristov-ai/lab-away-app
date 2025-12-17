import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCheckout } from '../../context/CheckoutContext';
import { GAItem, trackAddShippingInfo, trackGAEvent } from '../../utils/analytics';
import { BillingFormHandle } from '../../components/checkout/shipping/BillingForm';
import { ShippingFormHandle } from '../../components/checkout/shipping/ShippingForm';
import { useCheckoutSummary } from './useCheckoutSummary';

export function useShippingPage() {
  const { shippingData, setShippingData, setBillingData } = useCheckout();
  const [showBilling, setShowBilling] = useState(false);
  const navigate = useNavigate();
  const { enrichedItems } = useCheckoutSummary();
  const billingFormRef = useRef<BillingFormHandle>(null);
  const shippingFormRef = useRef<ShippingFormHandle>(null);

  useEffect(() => {
    if (enrichedItems.length === 0) {
      navigate('/');
    }
  }, [enrichedItems, navigate]);

  useEffect(() => {
    // Track when shipping page is viewed
    trackGAEvent('begin_checkout', {
      step: 2,
      description: 'Shipping page viewed',
    });
  }, []);

  const handleCheckboxChange = (checked: boolean) => {
    setShowBilling(checked);
  };

  const gaItems: GAItem[] = enrichedItems.map((item) => ({
    item_id: item.slug, 
    item_name: item.name,
    price: item.price,
    quantity: item.quantity,
    item_category: item.categories?.[0]?.name,
    item_category2: item.categories?.[1]?.name,
  }));


  const handleNextClick = () => {
    const isShippingValid = shippingFormRef.current?.validate() ?? false;
    const isBillingValid = !showBilling || (billingFormRef.current?.validate() ?? false);
    trackAddShippingInfo(gaItems);
    if (!isBillingValid || !isShippingValid) return;

    if (!showBilling) {
      setBillingData(shippingData);
    }

    trackGAEvent('checkout_progress', {
      step: 2,
      action: 'Shipping details submitted',
    });

    navigate('/payment');
  };

  return {
    shippingData,
    setBillingData,
    setShippingData,
    showBilling,
    handleCheckboxChange,
    handleNextClick,
    billingFormRef,
    shippingFormRef,
  };
}
