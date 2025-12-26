import React from 'react';
import './CookieToggle.css';
import { useTranslation } from 'react-i18next';

interface CookieToggleProps {
  label: string;
  value: boolean;
  onChange: (newValue: boolean) => void;
}

const CookieToggle: React.FC<CookieToggleProps> = ({ label, value, onChange }) => {
  const { t } = useTranslation();
  return (
    <div className="cookie-toggle">
      <label className="cookie-slider">
        <input
          type="checkbox"
          checked={value}
          onChange={() => onChange(!value)}
          aria-label={label}
        />
        <span className="slider-circle" />
      </label>
            <span className={`toggle-label ${value ? 'enabled' : 'disabled'}`}>
        {label}: {value ? t("cookie.enabled") : t("cookie.disabled")}
      </span>
    </div>
  );
};

export default CookieToggle;
