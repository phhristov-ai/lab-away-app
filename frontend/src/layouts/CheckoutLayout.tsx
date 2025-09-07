import React from 'react';

import OrderSummaryImg from '../assets/images/checkout/Order-Summary.png';
import BillingShippingImg from '../assets/images/checkout/Billing-Shipping.png';
import OrderPaymentImg from '../assets/images/checkout/Order-Payment.png';
import './CheckoutLayout.css';
import { useLocation } from 'react-router-dom';
import CheckoutGraph from '../components/checkout/cart/CheckoutGraph';
import { useTranslation } from 'react-i18next';

type CheckoutLayoutProps = {
  children: React.ReactNode;
};

const CheckoutLayout: React.FC<CheckoutLayoutProps> = ({ children }) => {
  const location = useLocation();
  const { t } = useTranslation();
  const currentPath = location.pathname;

  const steps = [
    {
      id: 'order-summary',
      label: t('checkout.steps.orderSummary'),
      imgSrc: OrderSummaryImg,
      isSelected: currentPath === '/cart',
    },
    {
      id: 'billing-shipping',
      label: t('checkout.steps.billingShipping'),
      imgSrc: BillingShippingImg,
      isSelected: currentPath === '/checkout',
    },
    {
      id: 'order-payment',
      label: t('checkout.steps.orderPayment'),
      imgSrc: OrderPaymentImg,
      isSelected: currentPath === '/payment',
    },
    {
      id: 'thank-you',
      label: t('checkout.steps.thankYou'),
      imgSrc: OrderPaymentImg,
      isSelected: currentPath === '/success',
    },
  ];

  return (
    <div className="checkout-layout">
      <CheckoutGraph steps={steps} />
      <div className="checkout-content">{children}</div>
    </div>
  );
};

export default CheckoutLayout;
