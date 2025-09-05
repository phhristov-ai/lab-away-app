
import { useTranslation } from 'react-i18next';
import BillingForm from '../components/checkout/shipping/BillingForm';
import CustomButton from '../components/checkout/shipping/CustomButton';
import ShippingForm from '../components/checkout/shipping/ShippingForm';
import CheckoutLayout from '../layouts/CheckoutLayout';
import './ShippingPage.css';
import { useShippingPage } from '../hooks/useShippingPage';


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

  return (
    <CheckoutLayout>
      <div>
        <ShippingForm
          ref={shippingFormRef}
          onToggleShipping={handleCheckboxChange}
          onChange={setShippingData}
        />
        {showBilling && (
          <BillingForm ref={billingFormRef} onChange={setBillingData} />
        )}
        <div className="align-right">
          <CustomButton
            label={t('checkout.actions.next')}
            onClick={handleNextClick}
            variant="primary"
          />
        </div>
      </div>
    </CheckoutLayout>
  );
};

export default ShippingPage;
