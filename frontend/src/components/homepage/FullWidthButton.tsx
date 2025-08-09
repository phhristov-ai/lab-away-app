import React from 'react';
import './FullWidthButton.css';
import { Link } from 'react-router-dom';

interface FullWidthButtonProps {
  text: string;
  to: string;
}

const FullWidthButton: React.FC<FullWidthButtonProps> = ({ text, to }) => {
  return (
    <Link to={to} className="full-width-button-link">
      <button className="full-width-button">{text}</button>
    </Link>
  );
};

export default FullWidthButton;
