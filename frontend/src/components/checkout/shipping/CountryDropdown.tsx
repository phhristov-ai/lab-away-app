import React from 'react';
import './CountryDropdown.css';

type Country = {
  code: string;
  name: string;
};

type CountryDropdownProps = {
  id: string;
  label: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  error?: string;
  options: Country[];
};

const CountryDropdown: React.FC<CountryDropdownProps> = ({
  id,
  label,
  onChange,
  error,
  value,
  options,
}) => {

  return (
    <div className={`form-group ${error ? 'has-error' : ''}`}>
      <label htmlFor={id}>{label}</label>
      <select id={id} name={id} className="input-cell" value={value} onChange={onChange}>
        <option value="">{label}</option>
        {options.map(({ code, name }) => (
          <option key={code} value={code}>
            {name}
          </option>
        ))}
      </select>
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default CountryDropdown;