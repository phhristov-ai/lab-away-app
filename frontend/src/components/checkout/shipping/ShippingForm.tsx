import { t } from 'i18next';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import { useShippingForm } from '../../../hooks/checkout/useShippingForm';
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
      handleShippingCountryChange,
      countryOptions
    } = useShippingForm(onChange, initialValues);

    useImperativeHandle(ref, () => ({
      validate,
    }));
    useEffect(() => {
      onToggleShipping(useDifferentBilling);
    }, [useDifferentBilling, onToggleShipping]);

    return (
      <div className="address-form">
        <h2 className="section-title">{t('checkout.shipping.title')}</h2>
        <form>
          <div className="form-row">
            <FormField id="email" value={formData.email || ''} label={t('checkout.email')} onChange={handleChange} error={errors.email} />
          </div>
          <div className="form-row">
            <FormField id="fullName" value={formData.fullName || ''} label={t('checkout.shipping.fields.fullName')} onChange={handleChange} error={errors.fullName} />
            <CountryDropdown
              id="countryCode"
              label={t('checkout.shipping.fields.country')}
              value={formData.countryCode}
              error={errors.countryCode}
              options={countryOptions}
              onChange={handleShippingCountryChange}
            />
          </div>

          <div className="form-row">
            <PhoneField
              id="phone"
              value={formData.phone || ''}
              label={t('checkout.shipping.fields.phone')}
              onChange={handleChange}
              error={errors.phone}
              countryCode={formData.countryCode}
            />
            <FormField id="address" value={formData.address || ''} label={t('checkout.shipping.fields.address')} onChange={handleChange} error={errors.address} />
          </div>
          <div className="form-row">
            <FormField id="postcode" value={formData.postcode || ''} label={t('checkout.shipping.fields.postcode')} onChange={handleChange} error={errors.postcode} />
            <FormField id="city" value={formData.city || ''} label={t('checkout.shipping.fields.city')} onChange={handleChange} error={errors.city} />
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

