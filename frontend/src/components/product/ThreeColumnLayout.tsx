import React from 'react';
import './ThreeColumnLayout.css';

interface ColumnContent {
  icon: string; 
  header: string; 
  description: string;
}

interface ThreeColumnLayoutProps {
  columns: ColumnContent[];
}

const ThreeColumnLayout: React.FC<ThreeColumnLayoutProps> = ({ columns }) => {
  
  return (
    <div className="three-column-container">
      {columns.map((column) => (
        <div key={column.header} className="column">
          <div className="icon">
            <img src={column.icon} alt={column.header} className="icon-image" />
          </div>
          <div className="header">{column.header}</div>
          <div className="description">{column.description}</div>
        </div>
      ))}
    </div>
  );
};

export default ThreeColumnLayout;
