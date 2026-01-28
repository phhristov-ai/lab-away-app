import React from 'react';
import './AvailabilityToggle.css';

interface ProductAvailabilityToggleProps {
  active?: boolean;
  onChange: (value: boolean) => void;
}

const ProductAvailabilityToggle: React.FC<ProductAvailabilityToggleProps> = ({
  active,
  onChange,
}) => {
  const isActiveDefined = active !== undefined;

  return (
    <div className="availability-toggle">
      <span className="label">Availability</span>

      <div className="toggle-group">
        <button
          type="button"
          className={`toggle-btn ${isActiveDefined && active ? 'active' : ''}`}
          onClick={() => onChange(true)}
        >
          In stock
        </button>

        <button
          type="button"
          className={`toggle-btn ${isActiveDefined && !active ? 'active' : ''}`}
          onClick={() => onChange(false)}
        >
          Out of stock
        </button>
      </div>
    </div>
  );
};

export default ProductAvailabilityToggle;
