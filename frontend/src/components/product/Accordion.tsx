import React, { useState } from 'react';
import './Accordion.css';

interface AccordionItem {
  title: string;
  content: string | React.ReactNode;
}

interface AccordionProps {
  items: AccordionItem[];
}

const Accordion: React.FC<AccordionProps> = ({ items }) => {
  const [openIndex, setOpenIndex] = useState<number | null>(null);

  const toggle = (index: number) => {
    setOpenIndex(prev => (prev === index ? null : index));
  };

  return (
    <div className="accordion">
      {items.map((item, index) => (
        <div className="accordion-item" key={item.title}>
          <button className="accordion-title" onClick={() => toggle(index)}>
            {item.title}
            <span className={`arrow ${openIndex === index ? 'open' : ''}`}>
              {openIndex === index ? '−' : '+'}
            </span>
          </button>
          <div className={`accordion-content ${openIndex === index ? 'show' : ''}`}>
            {item.content}
          </div>
        </div>
      ))}
    </div>
  );
};

export default Accordion;
