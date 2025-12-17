import React from 'react';
import './LabAwayButton.css';

interface LabAwayButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  className?: string;
}

const LabAwayButton: React.FC<LabAwayButtonProps> = ({ children, className = '', ...props }) => {
  return (
    <button className={`labaway-button ${className}`} {...props}>
      {children}
    </button>
  );
};

export default LabAwayButton;
