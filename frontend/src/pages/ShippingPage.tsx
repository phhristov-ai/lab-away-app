
import { useTranslation } from 'react-i18next';
import BillingForm from '../components/checkout/shipping/BillingForm';
import CustomButton from '../components/checkout/shipping/CustomButton';
import ShippingForm from '../components/checkout/shipping/ShippingForm';
import CheckoutLayout from '../layouts/CheckoutLayout';
import './ShippingPage.css';
import { useShippingPage } from '../hooks/useShippingPage';
import OrderSummary from '../components/checkout/payment/OrderSummary';
import { useCheckout } from '../context/CheckoutContext';
import { useCheckoutSummary } from '../hooks/useCheckoutSummary';

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

  return (
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
  );
}
export default ShippingPage;
