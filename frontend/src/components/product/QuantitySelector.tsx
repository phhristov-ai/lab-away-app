import React from 'react';
import './QuantitySelector.css';

type QuantitySelectorProps = {
  value: number;
  onChange: (value: number) => void;
  active?: boolean;
  className?: string;
};

const QuantitySelector: React.FC<QuantitySelectorProps> = ({
  value,
  onChange,
  active = true,
  className
}) => {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(Number(e.target.value));
  };
  const isDisabled = !active;

  return (
    <div
      className={`quantity-selector-wrapper ${isDisabled ? 'disabled' : ''} ${className || ''}`}
    >
      <select
        id="quantity"
        className="quantity-dropdown"
        value={value}
        onChange={handleChange}
        disabled={!active}
        aria-disabled={!active}
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
