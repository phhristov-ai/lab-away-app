import { t } from 'i18next';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import { useShippingForm } from '../../../hooks/useShippingForm';
import CountryDropdown from './CountryDropdown';
import FormField from './FormField';
import './ShippingForm.css';
import PhoneField from './PhoneField';


type ShippingFormProps = {
  onToggleShipping: (checked: boolean) => void;
  onChange: (data: Record<string, string>) => void;
  initialValues?: Record<string, string>;
};

export type ShippingFormHandle = {
  validate: () => boolean;
};

const ShippingForm = forwardRef<ShippingFormHandle, ShippingFormProps>(
  ({ onToggleShipping, onChange, initialValues = {} }, ref) => {
    const {
      errors,
      handleChange,
      validate,
      formData,
      useDifferentBilling,
      handleCheckboxChange,
    } = useShippingForm(onChange, initialValues);
    
    useImperativeHandle(ref, () => ({
      validate,
    }));

      useEffect(() => {
        onToggleShipping(useDifferentBilling);
      }, [useDifferentBilling, onToggleShipping]);

    return (
      <div className="address-form">
        <h2>{t('checkout.shipping.title')}</h2>
        <form>
          <div className="form-row">
            <FormField id="firstName" value={formData.firstName || ''} label={t('checkout.shipping.fields.firstName')} onChange={handleChange} error={errors.firstName} />
            <FormField id="lastName" value={formData.lastName || ''} label={t('checkout.shipping.fields.lastName')} onChange={handleChange} error={errors.lastName} />

          </div>
          <div className="form-row">
            <FormField id="email" value={formData.email || ''} label={t('checkout.email')} onChange={handleChange} error={errors.email} />
            <PhoneField
              id="phone"
              value={formData.phone || ''}
              label={t('checkout.shipping.fields.phone')}
              onChange={handleChange}
              error={errors.phone}
            />
          </div>
          <div className="form-row">
            <CountryDropdown id="country" value={formData.country || ''} label={t('checkout.shipping.fields.country')} onChange={handleChange} error={errors.country} />
            <FormField id="address" value={formData.address || ''} label={t('checkout.shipping.fields.address')} onChange={handleChange} error={errors.address} />
          </div>
          <div className="form-row">
            <FormField id="city" value={formData.city || ''} label={t('checkout.shipping.fields.city')} onChange={handleChange} error={errors.city} />
            <FormField id="postcode" value={formData.postcode || ''} label={t('checkout.shipping.fields.postcode')} onChange={handleChange} error={errors.postcode} />
          </div>
          <div className="checkbox-group">
            <input
              type="checkbox"
              id="differentBilling"
              checked={useDifferentBilling}
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

