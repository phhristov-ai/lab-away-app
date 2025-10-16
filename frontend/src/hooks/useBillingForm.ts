import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getEUCountryOptions } from './getEUCountryOptions';
import i18n from '../i18n/i18n';

type FormData = Record<string, string>;
type Errors = Record<string, string>;

const requiredFields = [
  'fullName',
  'phone',
  'countryCode',
  'address',
  'city',
  'postcode'
];

type SyntheticOrFakeEvent =
  | React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  | { target: { id: string; value: string } };

export function useBillingForm(onChange: (data: FormData) => void, initialValues: Record<string, string>) {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<FormData>(initialValues);
  const [errors, setErrors] = useState<Errors>({});
  const countryOptions = getEUCountryOptions(i18n.language);
  const handleChange = (e: SyntheticOrFakeEvent) => {
    const { id, value } = e.target;
    const updatedData = { ...formData, [id]: value };
    setFormData(updatedData);
    onChange(updatedData);

    if (value.trim()) {
      setErrors((prevErrors) => {
        const { [id]: removed, ...rest } = prevErrors;
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

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = t('form.errors.invalidEmail');
    }

    if (formData.phone && !/^\+?\d+$/.test(formData.phone)) {
      newErrors.phone = t('form.errors.invalidPhone');
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

    const handleCountryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
      const selectedCode = e.target.value;
      const selectedCountry = countryOptions.find((c) => c.code === selectedCode);
      const selectedName = selectedCountry ? selectedCountry.name : '';
  
      const updatedData = {
        ...formData,
        countryCode: selectedCode,
        countryName: selectedName,
      };
      setFormData(updatedData);
      onChange(updatedData);
  
      setErrors(prevErrors => {
        const { countryCode, countryName, ...rest } = prevErrors;
        return rest;
      });
    };

  return { formData, errors, handleChange, validate, handleCountryChange, countryOptions};
}
