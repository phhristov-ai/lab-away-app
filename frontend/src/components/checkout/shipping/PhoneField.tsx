import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import './PhoneField.css';

type PhoneFieldProps = {
  id: string;
  label: string;
  value: string;
  onChange: (e: { target: { id: string; value: string } }) => void;
  error?: string;
  countryCode?: string;
};

const PhoneField: React.FC<PhoneFieldProps> = ({ id, label, value, onChange, error, countryCode }) => {
  return (
    <div className={`form-group ${error ? 'has-error' : ''}`}>
      <label htmlFor={id}>{label}</label>

      <PhoneInput
        key={countryCode}
        country={countryCode?.toLowerCase() || 'de'}
        value={value}
        onChange={(val) => {
          onChange({ target: { id, value: val } });
        }}
        inputProps={{
          id,
          name: id,
          required: true,
        }}
        enableSearch
        placeholder="Enter phone number"
        inputClass="input-cell"
        specialLabel=""
        autoFormat={false}
      />

      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default PhoneField;
