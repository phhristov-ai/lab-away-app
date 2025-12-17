import OrderSummary from "../../components/checkout/payment/OrderSummary";
import CheckoutLayout from "../../layouts/CheckoutLayout";
import StripeProviderWrapper from "../../components/checkout/payment/StripeProviderWrapper";
import CreditCardForm from "../../components/checkout/payment/CreditCardForm";
import './PaymentPage.css';
import PayPalForm from "../../components/checkout/payment/PayPalForm";
import { useCheckout } from "../../context/CheckoutContext";
import { useTranslation } from 'react-i18next';
import cardIcon from '../../assets/icons/payment/card.svg';
import paypal1 from '../../assets/icons/payment/PayPal1.webp';
import paypal2 from '../../assets/icons/payment/PayPal2.webp';
import { Helmet } from "react-helmet";
import { usePaymentPage } from "../../hooks/checkout/usePaymentPage";

const PaymentPage = () => {
  const { t } = useTranslation();
  const { shippingData } = useCheckout();
  const {
    paymentMethod,
    setPaymentMethod,
    enrichedItems,
    subtotalValue,
    shippingCost,
    total,
  } = usePaymentPage();

  const pageTitle = t('checkout.payment.title') || "Payment Information";
  const pageDescription = t('checkout.payment.description') || "Select your preferred payment method and complete your purchase.";

  return (
    <>
      <Helmet>
        <title>{pageTitle} | Lab-Away</title>
        <meta name="description" content={pageDescription} />
        <meta property="og:title" content={pageTitle} />
        <meta property="og:description" content={pageDescription} />
      </Helmet>
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
                  <span className="label-with-icon">
                    <img src={cardIcon} alt="Card icon" className="icon-before-label" loading="lazy"/>
                    {t('checkout.payment.creditCard.methodLabel')}
                  </span>
                </label>
                <label className={`payment-method ${paymentMethod === 'paypal' ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="payment-method"
                    value="paypal"
                    checked={paymentMethod === 'paypal'}
                    onChange={() => setPaymentMethod('paypal')}
                  />
                  <span className="paypal-icons">
                    <img src={paypal1} alt="PayPal icon 1" className="paypal-icon" loading="lazy"/>
                    <img src={paypal2} alt="PayPal icon 2" className="paypal-icon" loading="lazy"/>
                  </span>
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
                  name: `${shippingData.fullName}`,
                  address: shippingData.address,
                  phone: shippingData.phone,
                  city: `${shippingData.postcode} ${shippingData.city}`,
                  countryName: shippingData.countryName,
                  countryCode: shippingData.CountryCode,
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
    </>
  );
};

export default PaymentPage;
