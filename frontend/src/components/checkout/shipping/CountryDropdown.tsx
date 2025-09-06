import React from 'react';
import './CountryDropdown.css';
import { useTranslation } from 'react-i18next';
import { getEUCountryOptions } from '../../../hooks/getEUCountryOptions';

type CountryDropdownProps = {
  id: string;
  label: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  error?: string;
};

const CountryDropdown: React.FC<CountryDropdownProps> = ({ id, label, onChange, error, value }) => {
  const { i18n } = useTranslation();
  const countryOptions = getEUCountryOptions(i18n.language);

  return (
    <div className={`form-group ${error ? 'has-error' : ''}`}>
      <label htmlFor={id}>{label}</label>
      <select id={id} name={id} className="input-cell" value={value} onChange={onChange}>
        <option value="">{label}</option>
        {countryOptions.map(({ code, name }) => (
          <option key={code} value={code}>{name}</option>
        ))}
      </select>
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default CountryDropdown;
