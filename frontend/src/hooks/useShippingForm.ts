import { useState } from 'react';
import { useTranslation } from 'react-i18next';

type FormData = Record<string, string>;
type Errors = Record<string, string>;

const requiredFields = ['firstName', 'lastName', 'country', 'email', 'phone', 'address', 'city', 'postcode'];

export function useShippingForm(onChange: (data: FormData) => void) {
  const { t } = useTranslation();
  const [formData, setFormData] = useState<FormData>({});
  const [errors, setErrors] = useState<Errors>({});

type SyntheticOrFakeEvent =
  | React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  | { target: { id: string; value: string } };

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

  return { formData, errors, handleChange, validate };
}
