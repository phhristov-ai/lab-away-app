import React from 'react';
import './CookieToggle.css';

interface CookieToggleProps {
  label: string;
  value: boolean;
  onChange: (newValue: boolean) => void;
}

const CookieToggle: React.FC<CookieToggleProps> = ({ label, value, onChange }) => {
  return (
    <div className="cookie-toggle">
      <label className="cookie-slider">
        <input
          type="checkbox"
          checked={value}
          onChange={() => onChange(!value)}
        />
        <span className="slider-circle" />
      </label>
      <span className={`toggle-label ${value ? 'enabled' : 'disabled'}`}>
        {label}: {value ? 'Enabled' : 'Disabled'}
      </span>
    </div>
  );
};

export default CookieToggle;
