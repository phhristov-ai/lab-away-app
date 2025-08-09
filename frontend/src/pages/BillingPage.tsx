import BillingForm from "../components/checkout/billing/BillingForm";
import ShippingForm from "../components/checkout/billing/ShippingForm";
import CustomButton from "../components/checkout/billing/CustomButton";
import './BillingPage.css';
import CheckoutLayout from "../layouts/CheckoutLayout";
import { useTranslation } from 'react-i18next';
import { useBillingPage } from "../hooks/useBillingPage";

const BillingPage = () => {
  const {
    showShipping,
    handleCheckboxChange,
    handleNextClick,
    setBillingData,
    setShippingData,
    billingFormRef,
    shippingFormRef,
  } = useBillingPage();

  const { t } = useTranslation();

  return (
    <CheckoutLayout>
      <div>
        <BillingForm
          ref={billingFormRef}
          onToggleShipping={handleCheckboxChange}
          onChange={setBillingData}
        />
        {showShipping && (
          <ShippingForm ref={shippingFormRef} onChange={setShippingData} />
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

export default BillingPage;
