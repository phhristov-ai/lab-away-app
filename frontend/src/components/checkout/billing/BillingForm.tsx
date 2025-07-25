import { useState } from 'react';
import './BillingForm.css';
import FormField from './FormField';
import { useTranslation } from 'react-i18next';

type BillingFormProps = {
  onToggleShipping: (checked: boolean) => void;
  onChange: (data: Record<string, string>) => void;
};

const BillingForm: React.FC<BillingFormProps> = ({ onToggleShipping, onChange }) => {
  const [formData, setFormData] = useState<Record<string, string>>({});
  const { t } = useTranslation();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const updatedData = { ...formData, [e.target.id]: e.target.value };
    setFormData(updatedData);
    onChange(updatedData);
  };

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onToggleShipping(e.target.checked);
  };
  

  return (
    <div className="address-form">
      <h2>{t('checkout.billing.title')}</h2>
      <form>
        <div className="form-row">
          <FormField id="firstName" label={t('checkout.billing.fields.firstName')} onChange={handleChange} />
          <FormField id="lastName" label={t('checkout.billing.fields.lastName')} onChange={handleChange} />
        </div>
        <div className="form-row">
          <FormField id="email" label={t('checkout.billing.fields.email')} type="email" onChange={handleChange} />
          <FormField id="phone" label={t('checkout.billing.fields.phone')} type="tel" onChange={handleChange} />
        </div>
        <div className="form-row">
          <FormField id="country" label={t('checkout.billing.fields.country')} onChange={handleChange} />
          <FormField id="address" label={t('checkout.billing.fields.address')} onChange={handleChange} />
        </div>
        <div className="form-row">
          <FormField id="city" label={t('checkout.billing.fields.city')} onChange={handleChange} />
          <FormField id="postcode" label={t('checkout.billing.fields.postcode')} onChange={handleChange} />
        </div>
        <div className="checkbox-group">
        <input
            type="checkbox"
            id="shipToDifferent"
            name="shipToDifferent"
            onChange={handleCheckboxChange}
          />
          <label htmlFor="shipToDifferent">{t('checkout.billing.shipToDifferent')}</label>
        </div>
      </form>
    </div>
  );
};

export default BillingForm;
