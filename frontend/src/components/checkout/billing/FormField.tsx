import React from 'react';

type FormFieldProps = {
  id: string;
  label: string;
  type?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
};

const FormField: React.FC<FormFieldProps> = ({ id, label, type = 'text', onChange }) => (
  <div className="form-group">
    <label htmlFor={id}>{label}</label>
    <input
      type={type}
      className="input-cell"
      id={id}
      name={id}
      onChange={onChange}
    />
  </div>
);

export default FormField;
