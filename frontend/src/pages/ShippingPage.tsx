
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
            <BillingForm ref={billingFormRef} onChange={setBillingData} initialValues={billingData}/>
          )}
          <div className="align-right">
            <CustomButton
              label={t('checkout.actions.next')}
              onClick={handleNextClick}
              variant="primary"
            />
          </div>
        </div>

        <div className="summary-section">
          <OrderSummary
            items={enrichedItems}
            shipping={{
              name: `${shippingData.firstName || ''} ${shippingData.lastName || ''}`,
              address: shippingData.address || '',
              phone: shippingData.phone || '',
              city: `${shippingData.postcode || ''} ${shippingData.city || ''}`,
              country: shippingData.country || '',
              email: shippingData.email || '',
            }}
            subtotal={subtotalValue}
            shippingCost={shippingCost}
            total={total}
          />
        </div>
      </div>
    </CheckoutLayout>
  );
}
export default ShippingPage;
