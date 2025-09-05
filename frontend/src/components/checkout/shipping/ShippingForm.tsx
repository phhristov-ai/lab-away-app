import { t } from 'i18next';
import { forwardRef, useImperativeHandle } from 'react';
import { useShippingForm } from '../../../hooks/useShippingForm';
import CountryDropdown from './CountryDropdown';
import FormField from './FormField';
import './ShippingForm.css';
import PhoneField from './PhoneField';


type ShippingFormProps = {
  onToggleShipping: (checked: boolean) => void;
  onChange: (data: Record<string, string>) => void;
};

export type ShippingFormHandle = {
  validate: () => boolean;
};

const ShippingForm = forwardRef<ShippingFormHandle, ShippingFormProps>(
  ({ onToggleShipping, onChange }, ref) => {
    const { errors, handleChange, validate } = useShippingForm(onChange);

    useImperativeHandle(ref, () => ({
      validate
    }));

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      onToggleShipping(e.target.checked);
    };

    return (
      <div className="address-form">
        <h2>{t('checkout.shipping.title')}</h2>
        <form>
          <div className="form-row">
            <FormField id="firstName" label={t('checkout.shipping.fields.firstName')} onChange={handleChange} error={errors.firstName} />
            <FormField id="lastName" label={t('checkout.shipping.fields.lastName')} onChange={handleChange} error={errors.lastName} />

          </div>
          <div className="form-row">
            <FormField id="email" label={t('checkout.email')} onChange={handleChange} error={errors.email} />
            <PhoneField
              id="phone"
              label={t('checkout.shipping.fields.phone')}
              value={''}
              onChange={handleChange}
              error={errors.phone}
            />
          </div>
          <div className="form-row">
            <CountryDropdown id="country" label={t('checkout.shipping.fields.country')} onChange={handleChange} error={errors.country} />
            <FormField id="address" label={t('checkout.shipping.fields.address')} onChange={handleChange} error={errors.address} />
          </div>
          <div className="form-row">
            <FormField id="city" label={t('checkout.shipping.fields.city')} onChange={handleChange} error={errors.city} />
            <FormField id="postcode" label={t('checkout.shipping.fields.postcode')} onChange={handleChange} error={errors.postcode} />
          </div>
          <div className="checkbox-group">
            <input
              type="checkbox"
              id="differentBilling"
              name="differentBilling"
              onChange={handleCheckboxChange}
            />
            <label htmlFor="differentBilling">
              {t('checkout.shipping.useDifferentBilling')}
            </label>
          </div>
        </form>
      </div>
    );
  }
);

export default ShippingForm;
