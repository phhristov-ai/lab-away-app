import React, { useState } from 'react';
import FormField from './FormField';
import './BillingForm.css';
import { useTranslation } from 'react-i18next';

type ShippingFormProps = {
  onChange: (data: Record<string, string>) => void;
};

const ShippingForm: React.FC<ShippingFormProps> = ({ onChange }) => {
  const [formData, setFormData] = useState<Record<string, string>>({});
  const { t } = useTranslation();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const updatedData = { ...formData, [e.target.id]: e.target.value };
    setFormData(updatedData);
    onChange(updatedData);
  };

  return (
    <div className="address-form">
      <h2>{t('checkout.shipping.title')}</h2>
      <form>
        <div className="form-row">
          <FormField id="firstName" label={t('checkout.shipping.fields.firstName')} onChange={handleChange} />
          <FormField id="lastName" label={t('checkout.shipping.fields.lastName')} onChange={handleChange} />
        </div>
        <div className="form-row">
          <FormField id="country" label={t('checkout.shipping.fields.country')} onChange={handleChange} />
          <FormField id="address" label={t('checkout.shipping.fields.address')} onChange={handleChange} />
        </div>
        <div className="form-row">
          <FormField id="city" label={t('checkout.shipping.fields.city')} onChange={handleChange} />
          <FormField id="postcode" label={t('checkout.shipping.fields.postcode')} onChange={handleChange} />
        </div>
      </form>
    </div>
  );
};

export default ShippingForm;

