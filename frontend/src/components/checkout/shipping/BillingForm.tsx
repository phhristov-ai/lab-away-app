import FormField from './FormField';
import './ShippingForm.css';
import CountryDropdown from './CountryDropdown';
import {
  forwardRef,
  useImperativeHandle
} from 'react';
import { useBillingForm } from '../../../hooks/useBillingForm';
import { t } from 'i18next';

export type BillingFormHandle = {
  validate: () => boolean;
};

type BillingFormProps = {
  onChange: (data: Record<string, string>) => void;
};

const BillingForm = forwardRef<BillingFormHandle, BillingFormProps>(({ onChange }, ref) => {
  const { errors, handleChange, validate } = useBillingForm(onChange);
  useImperativeHandle(ref, () => ({ validate }));

  return (
    <div className="address-form">
      <h2>{t('checkout.billing.title')}</h2>
      <form>
        <div className="form-row">
          <FormField id="firstName" label={t('checkout.billing.fields.firstName')} onChange={handleChange} error={errors.firstName} />
          <FormField id="lastName" label={t('checkout.billing.fields.lastName')} onChange={handleChange} error={errors.lastName} />
        </div>
        <div className="form-row">
          <CountryDropdown id="country" label={t('checkout.billing.fields.country')} onChange={handleChange} error={errors.country} />
          <FormField id="address" label={t('checkout.billing.fields.address')} onChange={handleChange} error={errors.address} />
        </div>
        <div className="form-row">
          <FormField id="phone" label={t('checkout.shipping.fields.phone')} onChange={handleChange} error={errors.phone} />
        </div>
        <div className="form-row">
          <FormField id="city" label={t('checkout.billing.fields.city')} onChange={handleChange} error={errors.city} />
          <FormField id="postcode" label={t('checkout.billing.fields.postcode')} onChange={handleChange} error={errors.postcode} />
        </div>
      </form>
    </div>
  );
});

export default BillingForm;

