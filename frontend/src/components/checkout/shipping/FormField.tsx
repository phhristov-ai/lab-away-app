import React from 'react';

type FormFieldProps = {
  id: string;
  label: string;
  type?: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  error?: string;
};

const FormField: React.FC<FormFieldProps> = ({ id, label, type = 'text', value = '', onChange, error }) => (
  <div className={`form-group ${error ? 'has-error' : ''}`}>
    <label htmlFor={id}>{label}</label>
    <input
      type={type}
      className="input-cell"
      id={id}
      name={label}
      value={value}
      onChange={onChange}
    />
    {error && <p className="error-message">{error}</p>}
  </div>
);

export default FormField;

