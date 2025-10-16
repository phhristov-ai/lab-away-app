import React from 'react';
import './CustomButton.css';

type CustomButtonProps = {
  label: string;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
  variant?: 'primary' | 'secondary' | 'third'; 
  disabled?: boolean;
  fullWidth?: boolean;
};

const CustomButton: React.FC<CustomButtonProps> = ({
  label,
  onClick,
  type = 'button',
  variant = 'primary',
  disabled = false,
  fullWidth = false
}) => {
  return (
    <button
      type={type}
      className={`custom-button ${variant} ${fullWidth ? 'full-width' : ''}`}
      onClick={onClick}
      disabled={disabled}
    >
      {label}
    </button>
  );
};

export default CustomButton;
