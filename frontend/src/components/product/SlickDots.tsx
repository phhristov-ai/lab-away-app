import React from 'react';
import './SlickDots.css';

interface SlickDotsProps {
  count: number;
  currentIndex: number;
  onDotClick: (index: number) => void;
  keys: (string | number)[];
}



const SlickDots: React.FC<SlickDotsProps> = ({ count, currentIndex, onDotClick, keys }) => {
  return (
    <div className="slick-dots">
      {Array.from({ length: count }).map((_, index) => (
        <button
          key={keys[index]}
          className={`dot ${index === currentIndex ? 'active' : ''}`}
          onClick={() => onDotClick(index)}
          aria-label={`Go to slide ${index + 1}`}
        />
      ))}
    </div>
  );
};


export default SlickDots;
