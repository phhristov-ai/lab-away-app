import React from 'react';
import './QuantitySelector.css';

type QuantitySelectorProps = {
  value: number;
  onChange: (value: number) => void;
};

const QuantitySelector: React.FC<QuantitySelectorProps> = ({ value, onChange }) => {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(Number(e.target.value));
  };

  return (
    <div className="quantity-selector-wrapper">
      <select
        id="quantity"
        className="quantity-dropdown"
        value={value}
        onChange={handleChange}
      >
        {[1, 2, 3, 4, 5].map((qty) => (
          <option key={qty} value={qty}>
            {qty}
          </option>
        ))}
      </select>
    </div>
  );
};

export default QuantitySelector;
