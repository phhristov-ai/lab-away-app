
import { useTranslation } from 'react-i18next';
import BillingForm from '../../components/checkout/shipping/BillingForm';
import CustomButton from '../../components/checkout/shipping/CustomButton';
import ShippingForm from '../../components/checkout/shipping/ShippingForm';
import CheckoutLayout from '../../layouts/CheckoutLayout';
import './ShippingPage.css';
import { useShippingPage } from '../../hooks/checkout/useShippingPage';
import OrderSummary from '../../components/checkout/payment/OrderSummary';
import { useCheckout } from '../../context/CheckoutContext';
import { Helmet } from 'react-helmet';
import { useCheckoutSummary } from '../../hooks/checkout/useCheckoutSummary';

const ShippingPage = () => {
  const {
    showBilling,
    handleCheckboxChange,
    handleNextClick,
    setBillingData,
    setShippingData,
    billingFormRef,
    shippingFormRef,
  } = useShippingPage();

  const isMobile = () => window.innerWidth <= 768;
  const { t } = useTranslation();
  const { shippingData, billingData } = useCheckout();
  const { enrichedItems, subtotalValue, shippingCost, total } = useCheckoutSummary();

  const pageTitle = t('checkout.shipping.title') || "Shipping Information";
  const pageDescription = t('checkout.shipping.description') || "Please provide your shipping and billing information to complete your purchase.";

  console.log(shippingData);
  return (
    <>
      <Helmet>
        <title>{pageTitle} | Lab-Away</title>
        <meta name="description" content={pageDescription} />
        <meta property="og:title" content={pageTitle} />
        <meta property="og:description" content={pageDescription} />
      </Helmet>
      <CheckoutLayout>
        <div className="shipping-page-grid">
          <div className="form-section">
            <ShippingForm
              ref={shippingFormRef}
              onToggleShipping={handleCheckboxChange}
              onChange={setShippingData}
              initialValues={shippingData}
            />
            {showBilling && (
              <BillingForm ref={billingFormRef} onChange={setBillingData} initialValues={billingData} />
            )}
            <div className="align-right">
              {isMobile() && (
                <CustomButton
                  label={t('checkout.actions.next')}
                  onClick={handleNextClick}
                  variant="primary"
                  fullWidth={true}
                />
              )}
            </div>
          </div>
          <div className="summary-wrapper">
            <div className="summary-section">
              <OrderSummary
                items={enrichedItems}
                shipping={{
                  name: `${shippingData.fullName || ''}`,
                  address: shippingData.address || '',
                  phone: shippingData.phone || '',
                  city: `${shippingData.postcode || ''} ${shippingData.city || ''}`,
                  countryName: shippingData.countryName || '',
                  countryCode: shippingData.countryCode || '',
                  email: shippingData.email || '',
                }}
                subtotal={subtotalValue}
                shippingCost={shippingCost}
                total={total}
                showNextButton={true}
                onNextClick={handleNextClick}
              />
            </div>
          </div>
        </div>
      </CheckoutLayout>
    </>
  );
}
export default ShippingPage;
