import FormField from './FormField';
import './ShippingForm.css';
import CountryDropdown from './CountryDropdown';
import {
  forwardRef,
  useImperativeHandle
} from 'react';
import { useBillingForm } from '../../../hooks/useBillingForm';
import { t } from 'i18next';
import PhoneField from './PhoneField';

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
          <FormField id="fullName" value={formData.fullName || ''} label={t('checkout.billing.fields.fullName')} onChange={handleChange} error={errors.fullName} />
          <CountryDropdown
            id="countryCode"
            value={formData.countryCode}
            label={t('checkout.billing.fields.country')}
            error={errors.countryCode}
            options={countryOptions}
            onChange={handleCountryChange}
          />
        </div>
        <div className="form-row">
          <PhoneField
            id="phone"
            value={formData.phone || ''}
            label={t('checkout.shipping.fields.phone')}
            onChange={handleChange}
            error={errors.phone}
          />
          <FormField id="address" value={formData.address || ''} label={t('checkout.billing.fields.address')} onChange={handleChange} error={errors.address} />
        </div>
        <div className="form-row">

        </div>
        <div className="form-row">
          <FormField id="postcode" value={formData.postcode || ''} label={t('checkout.billing.fields.postcode')} onChange={handleChange} error={errors.postcode} />
          <FormField id="city" value={formData.city || ''} label={t('checkout.billing.fields.city')} onChange={handleChange} error={errors.city} />
        </div>
      </form>
    </div>
  );
});

export default BillingForm;

