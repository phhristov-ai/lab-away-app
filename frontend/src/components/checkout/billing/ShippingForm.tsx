import FormField from './FormField';
import './BillingForm.css';
import CountryDropdown from './CountryDropdown';
import {
  forwardRef,
  useImperativeHandle
} from 'react';
import { useShippingForm } from '../../../hooks/useShippingForm';
import { t } from 'i18next';

export type ShippingFormHandle = {
  validate: () => boolean;
};

type ShippingFormProps = {
  onChange: (data: Record<string, string>) => void;
};

const ShippingForm = forwardRef<ShippingFormHandle, ShippingFormProps>(({ onChange }, ref) => {
  const { errors, handleChange, validate } = useShippingForm(onChange);
  useImperativeHandle(ref, () => ({ validate }));

  return (
    <div className="address-form">
      <h2>{t('checkout.shipping.title')}</h2>
      <form>
        <div className="form-row">
          <FormField id="firstName" label={t('checkout.shipping.fields.firstName')} onChange={handleChange} error={errors.firstName} />
          <FormField id="lastName" label={t('checkout.shipping.fields.lastName')} onChange={handleChange} error={errors.lastName} />
        </div>
        <div className="form-row">
          <CountryDropdown id="country" label={t('checkout.shipping.fields.country')} onChange={handleChange} error={errors.country} />
          <FormField id="address" label={t('checkout.shipping.fields.address')} onChange={handleChange} error={errors.address} />
        </div>
        <div className="form-row">
          <FormField id="city" label={t('checkout.shipping.fields.city')} onChange={handleChange} error={errors.city} />
          <FormField id="postcode" label={t('checkout.shipping.fields.postcode')} onChange={handleChange} error={errors.postcode} />
        </div>
      </form>
    </div>
  );
});

export default ShippingForm;

