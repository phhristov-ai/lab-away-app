import React, { useImperativeHandle, forwardRef } from 'react';
import './BillingForm.css';
import FormField from './FormField';
import CountryDropdown from './CountryDropdown';
import { t } from 'i18next';
import { useBillingForm } from '../../../hooks/useBillingForm';

type BillingFormProps = {
  onToggleShipping: (checked: boolean) => void;
  onChange: (data: Record<string, string>) => void;
};

export type BillingFormHandle = {
  validate: () => boolean;
};

const BillingForm = forwardRef<BillingFormHandle, BillingFormProps>(
  ({ onToggleShipping, onChange }, ref) => {
    const { errors, handleChange, validate } = useBillingForm(onChange);

    useImperativeHandle(ref, () => ({
      validate
    }));

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      onToggleShipping(e.target.checked);
    };

    return (
      <div className="address-form">
        <h2>{t('checkout.billing.title')}</h2>
        <form>
          <div className="form-row">
            <FormField id="firstName" label={t('checkout.billing.fields.firstName')} onChange={handleChange} error={errors.firstName} />
            <FormField id="lastName" label={t('checkout.billing.fields.lastName')} onChange={handleChange} error={errors.lastName} />
          </div>
          <div className="form-row">
            <FormField id="email" label={t('checkout.billing.fields.email')} onChange={handleChange} error={errors.email} />
            <FormField id="phone" label={t('checkout.billing.fields.phone')} onChange={handleChange} error={errors.phone} />
          </div>
          <div className="form-row">
            <CountryDropdown id="country" label={t('checkout.billing.fields.country')} onChange={handleChange} error={errors.country} />
            <FormField id="address" label={t('checkout.billing.fields.address')} onChange={handleChange} error={errors.address} />
          </div>
          <div className="form-row">
            <FormField id="city" label={t('checkout.billing.fields.city')} onChange={handleChange} error={errors.city} />
            <FormField id="postcode" label={t('checkout.billing.fields.postcode')} onChange={handleChange} error={errors.postcode} />
          </div>
          <div className="checkbox-group">
            <input
              type="checkbox"
              id="shipToDifferent"
              name="shipToDifferent"
              onChange={handleCheckboxChange}
            />
            <label htmlFor="shipToDifferent">
              {t('checkout.billing.shipToDifferent')}
            </label>
          </div>
        </form>
      </div>
    );
  }
);

export default BillingForm;
