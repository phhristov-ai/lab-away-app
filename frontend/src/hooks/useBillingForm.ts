import { useState } from 'react';
import { useTranslation } from 'react-i18next';

type FormData = Record<string, string>;
type Errors = Record<string, string>;

const requiredFields = [
  'firstName',
  'lastName',
  'email',
  'phone',
  'country',
  'address',
  'city',
  'postcode'
];

export function useBillingForm(onChange: (data: FormData) => void) {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<FormData>({});
  const [errors, setErrors] = useState<Errors>({});

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
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

  return { formData, errors, handleChange, validate };
}
