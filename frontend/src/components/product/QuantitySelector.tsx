import React from 'react';
import './QuantitySelector.css';
import { useTranslation } from 'react-i18next';

type QuantitySelectorProps = {
  value: number;
  onChange: (value: number) => void;
};

const QuantitySelector: React.FC<QuantitySelectorProps> = ({ value, onChange }) => {
  const { t } = useTranslation();
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(Number(e.target.value));
  };

  return (
    <div>
      <label className="quantityLabel" htmlFor="quantity">{t('checkout.cart.columns.quantity')}</label>
      <select
        id="quantity"
        className="quantity-dropdown"
        value={value}
        onChange={handleChange}
      >
        {[1, 2, 3, 4, 5].map((qty) => (
          <option key={qty} value={qty}>{qty}</option>
        ))}
      </select>
    </div>
  );
};

export default QuantitySelector;
