import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getEUCountryOptions } from './getEUCountryOptions';
import i18n from '../i18n/i18n';

type FormData = Record<string, string>;
type Errors = Record<string, string>;

const requiredFields = ['fullName', 'countryCode', 'email', 'phone', 'address', 'city', 'postcode'];

export function useShippingForm(onChange: (data: FormData) => void, initialValues: Record<string, string>) {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<FormData>(initialValues);
  const [errors, setErrors] = useState<Errors>({});
  const countryOptions = getEUCountryOptions(i18n.language);

  type SyntheticOrFakeEvent =
    | React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
    | { target: { id: string; value: string } };


  const [useDifferentBilling, setUseDifferentBilling] = useState<boolean>(() => {
    if (initialValues.differentBilling !== undefined) {
      return Boolean(initialValues.differentBilling);
    }
    const saved = localStorage.getItem('differentBilling');
    return saved ? JSON.parse(saved) : false;
  });

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const checked = e.target.checked;
    setUseDifferentBilling(checked);
    localStorage.setItem('differentBilling', JSON.stringify(checked));
  };

  const handleChange = (e: SyntheticOrFakeEvent) => {
    const { id, value } = e.target;
    const updatedData = { ...formData, [id]: value };
    setFormData(updatedData);
    onChange(updatedData);
    if (value.trim()) {
      setErrors(prev => {
        const { [id]: removed, ...rest } = prev;
        return rest;
      });
    }
  };

  const validate = () => {
    const newErrors: Errors = {};
    requiredFields.forEach(field => {
      if (!formData[field]?.trim()) {
        newErrors[field] = t('form.errors.required');
      }
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleShippingCountryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedCode = e.target.value;
    const selectedCountry = countryOptions.find((c) => c.code === selectedCode);
    const selectedName = selectedCountry ? selectedCountry.name : '';
    
    const updatedData = {
      ...formData,
      countryCode: selectedCode,
      countryName: selectedName,
      phone: '',
    };
    setFormData(updatedData);
    onChange(updatedData);

    setErrors(prevErrors => {
      const { countryCode, countryName, ...rest } = prevErrors;
      return rest;
    });
  };

  return {
    formData,
    errors,
    handleChange,
    validate,
    useDifferentBilling,
    handleCheckboxChange,
    handleShippingCountryChange,
    countryOptions
  };
}
