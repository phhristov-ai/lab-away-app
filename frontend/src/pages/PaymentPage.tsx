import OrderSummary from "../components/checkout/payment/OrderSummary";
import CheckoutLayout from "../layouts/CheckoutLayout";
import StripeProviderWrapper from "../components/checkout/payment/StripeProviderWrapper";
import CreditCardForm from "../components/checkout/payment/CreditCardForm";
import { useState } from "react";
import './PaymentPage.css';
import PayPalForm from "../components/checkout/payment/PayPalForm";
import { useCheckout } from "../context/CheckoutContext";
import { useTranslation } from 'react-i18next';
import { useCheckoutSummary } from "../hooks/useCheckoutSummary";

const PaymentPage = () => {
  const { t } = useTranslation();
  const { shippingData } = useCheckout();
  const [paymentMethod, setPaymentMethod] = useState<'creditCard' | 'paypal'>('creditCard');
  const { enrichedItems, subtotalValue, shippingCost, total } = useCheckoutSummary();

  return (
    <CheckoutLayout>
      <div className="payment-container">
        <div className="payment-content">
          <div className="payment-left">
            <h1>{t('checkout.payment.title')}</h1>
            <div className="payment-methods">
              <label className={`payment-method ${paymentMethod === 'creditCard' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="payment-method"
                  value="creditCard"
                  checked={paymentMethod === 'creditCard'}
                  onChange={() => setPaymentMethod('creditCard')}
                />
                <span>{t('checkout.payment.creditCard.methodLabel')}</span>
              </label>
              <label className={`payment-method ${paymentMethod === 'paypal' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="payment-method"
                  value="paypal"
                  checked={paymentMethod === 'paypal'}
                  onChange={() => setPaymentMethod('paypal')}
                />
                <span>{t('checkout.payment.paypal.methodLabel')}</span>
              </label>
            </div>

            <div className="payment-form">
              {paymentMethod === 'creditCard' && (
                <StripeProviderWrapper>
                  <CreditCardForm />
                </StripeProviderWrapper>
              )}
              {paymentMethod === 'paypal' && <PayPalForm />}
            </div>
          </div>

          <div className="order-summary-container">
            <OrderSummary
              items={enrichedItems}
              shipping={{
                name: `${shippingData.firstName} ${shippingData.lastName}`,
                address: shippingData.address,
                phone: shippingData.phone,
                city: `${shippingData.postcode} ${shippingData.city}`,
                country: shippingData.country,
                email: shippingData.email,
              }}
              subtotal={subtotalValue}
              shippingCost={shippingCost}
              total={total}
            />
          </div>
        </div>
      </div>
    </CheckoutLayout>
  );
};

export default PaymentPage;
