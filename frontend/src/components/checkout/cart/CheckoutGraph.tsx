import React from 'react';
import CheckoutStep from './CheckoutGraphStep';
import './CheckoutGraph.css';

interface Step {
  id: string;
  label: string;
  imgSrc: string;
  isSelected: boolean;
}

interface CheckoutGraphProps {
  steps: Step[];
}

const CheckoutGraph: React.FC<CheckoutGraphProps> = ({ steps }) => {
  return (
    <div className="checkout-graph-container">
      <div className="graph-row">
        {steps.map((step) => (
          <CheckoutStep
            key={step.id}
            label={step.label}
            imgSrc={step.imgSrc}
            isSelected={step.isSelected}
            isLast={step.id === steps[steps.length - 1].id}
          />
        ))}
      </div>

      <div className="text-row">
          {steps.map((step) => (
            <span className="checkout-text-element" key={`label-${step.id}`}>
              <span className="checkout-text">{step.label}</span>
            </span>
          ))}
      </div>
    </div>
  );
};

export default CheckoutGraph;
