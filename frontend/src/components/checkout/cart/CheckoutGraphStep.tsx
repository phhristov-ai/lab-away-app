import React from 'react';
import './CheckoutGraph.css';

interface CheckoutGraphStepProps {
  label: string;
  imgSrc: string;
  isSelected: boolean;
  isLast: boolean;
}

const CheckoutGraphStep: React.FC<CheckoutGraphStepProps> = ({ label, imgSrc, isSelected, isLast }) => {
  return (
    <>
      <span className="checkout-element">
        <span className="checkout-icon">
          <img
            className={`sub-footer-image ${isSelected ? 'checkout-selected' : ''}`}
            src={imgSrc}
            alt={label}
            width="40px"
            height="40px"
          />
        </span>
      </span>
      {!isLast && <span className="checkout-line" />}
    </>
  );
};

export default CheckoutGraphStep;
