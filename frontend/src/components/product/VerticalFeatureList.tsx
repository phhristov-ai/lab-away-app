import React from 'react';
import './VerticalFeatureList.css';

interface FeatureItem {
  id: string;
  icon: string;
  text: string;
}

interface VerticalFeatureListProps {
  items: FeatureItem[];
}

const VerticalFeatureList: React.FC<VerticalFeatureListProps> = ({ items }) => {
  return (
    <div className="vertical-feature-list">
      {items.map((item) => (
        <div className="feature-item" key={item.id}>
          <img src={item.icon} alt="" className="feature-icon" loading="lazy"/>
          <span className="feature-text">{item.text}</span>
        </div>
      ))}
    </div>
  );
};

export default VerticalFeatureList;
