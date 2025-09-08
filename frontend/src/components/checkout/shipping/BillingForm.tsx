import FormField from './FormField';
import './ShippingForm.css';
import CountryDropdown from './CountryDropdown';
import {
  forwardRef,
  useImperativeHandle
} from 'react';
import { useBillingForm } from '../../../hooks/useBillingForm';
import { t } from 'i18next';
import { useShippingForm } from '../../../hooks/useShippingForm';

export type BillingFormHandle = {
  validate: () => boolean;
};

type BillingFormProps = {
  onChange: (data: Record<string, string>) => void;
  initialValues?: Record<string, string>;
};

const BillingForm = forwardRef<BillingFormHandle, BillingFormProps>(({ onChange, initialValues }, ref) => {
  const { errors, handleChange, validate, formData, handleCountryChange, countryOptions } = useBillingForm(onChange, initialValues || {});
  useImperativeHandle(ref, () => ({ validate }));

  return (
    <div className="address-form">
      <h2>{t('checkout.billing.title')}</h2>
      <form>
        <div className="form-row">
          <FormField id="firstName" value={formData.firstName || ''} label={t('checkout.billing.fields.firstName')} onChange={handleChange} error={errors.firstName} />
          <FormField id="lastName" value={formData.lastName || ''} label={t('checkout.billing.fields.lastName')} onChange={handleChange} error={errors.lastName} />
        </div>
        <div className="form-row">
          <CountryDropdown
            id="countryCode"
            value={formData.countryCode}
            label={t('checkout.billing.fields.country')}
            error={errors.countryCode}
            options={countryOptions}
            onChange={handleCountryChange}
          />
          <FormField id="address" value={formData.address || ''} label={t('checkout.billing.fields.address')} onChange={handleChange} error={errors.address} />
        </div>
        <div className="form-row">
          <FormField id="phone" value={formData.phone || ''} label={t('checkout.shipping.fields.phone')} onChange={handleChange} error={errors.phone} />
        </div>
        <div className="form-row">
          <FormField id="city" value={formData.city || ''} label={t('checkout.billing.fields.city')} onChange={handleChange} error={errors.city} />
          <FormField id="postcode" value={formData.postcode || ''} label={t('checkout.billing.fields.postcode')} onChange={handleChange} error={errors.postcode} />
        </div>
      </form>
    </div>
  );
});

export default BillingForm;

