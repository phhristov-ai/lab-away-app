import BillingForm from "../components/checkout/billing/BillingForm";
import { useState } from "react";
import ShippingForm from "../components/checkout/billing/ShippingForm";
import CustomButton from "../components/checkout/billing/CustomButton";
import { useNavigate } from 'react-router-dom';
import './BillingPage.css';
import CheckoutLayout from "../layouts/CheckoutLayout";
import { useCheckout } from "../context/CheckoutContext";
import { useTranslation } from 'react-i18next';

const BillingPage = () => {
  const { billingData, setBillingData, setShippingData } = useCheckout();
  const [showShipping, setShowShipping] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleCheckboxChange = (checked: boolean) => {
    setShowShipping(checked);
  };


  const handleNextClick = () => {
    if (!showShipping) {
      setShippingData(billingData);
    }
    navigate('/payment');
  };

    return <CheckoutLayout>
        <div>
        <BillingForm onToggleShipping={handleCheckboxChange} onChange={setBillingData} />
        {showShipping && <ShippingForm onChange={setShippingData} />}
        <div className="align-right">
        <CustomButton
            label={t('checkout.actions.next')}
            onClick={handleNextClick}
            variant="primary"
          />
        </div>
      </div>
    </CheckoutLayout>
  };
  
  export default BillingPage;
  