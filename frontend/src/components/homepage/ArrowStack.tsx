import React from 'react';
import firstIcon from '../../assets/icons/first-icon.svg';
import secondIcon from '../../assets/icons/second-icon.svg';
import thirdIcon from '../../assets/icons/third-icon.svg';
import forthIcon from '../../assets/icons/forth-icon.svg';


const MAX_ARROW_HEIGHT = 180;

interface ArrowStackProps {
  blueArrowHeights: string[];
  arrowWidth?: number; 
  iconSrc?: string;
  gapIconsSrc?: string[];
}

const ArrowStack: React.FC<ArrowStackProps> = ({
  blueArrowHeights,
  arrowWidth = 40,
  iconSrc = firstIcon,
  gapIconsSrc = [secondIcon, thirdIcon, forthIcon],
}) => {
  return (
    <div className="arrow-stack-container">
      {/* Icon above first arrow */}
      <img src={iconSrc} alt="First Icon" className="arrow-icon" loading="lazy"/>

      {/* Arrows container */}
      <div
        className="arrows-container"
        style={{ width: `${arrowWidth}px` }}
      >
        {/* Grey arrows */}
        <div className="arrows-stack arrows-grey">
          {[...Array(4)].map((_, i) => (
            <div
              key={`grey-${i}`}
              className="arrow-vertical grey-arrow"
              style={{ height: `${MAX_ARROW_HEIGHT}px` }}
            />
          ))}
        </div>

        {/* Blue arrows */}
        <div className="arrows-stack arrows-blue">
          {blueArrowHeights.map((height, i) => (
            <div
              key={`blue-${i}`}
              className="arrow-vertical blue-arrow"
              style={{ height }}
            />
          ))}
        </div>

        {/* Icons between arrows */}
        {gapIconsSrc.map((src, i) => (
          <img
            key={`gap-icon-${i}`}
            src={src}
            alt={`Gap icon ${i + 1}`}
            className={`gap-icon gap-icon-${i + 1}`}
          />
        ))}
      </div>
    </div>
  );
};

export default ArrowStack;
