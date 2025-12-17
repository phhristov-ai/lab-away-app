import React from 'react';
import './ScrollAnimatedArrows.css';
import ArrowStack from './ArrowStack';
import { useTranslation } from 'react-i18next';
import AnimationDescription from './AnimationDescription';
import useArrowAnimation from '../../hooks/utils/useArrowAnimation';


const ScrollAnimatedArrows: React.FC = () => {
  const { t } = useTranslation();

  const sectionRefs = React.useRef([
    React.createRef<HTMLDivElement>(),
    React.createRef<HTMLDivElement>(),
    React.createRef<HTMLDivElement>(),
    React.createRef<HTMLDivElement>(),
  ]).current;

  const MAX_ARROW_HEIGHT = 180;
  const ANIMATION_DURATION = 400;

  const arrowHeights = useArrowAnimation(sectionRefs, MAX_ARROW_HEIGHT, ANIMATION_DURATION);
  const blueArrowHeights = arrowHeights.map((height) => `${height}px`);

  return (
    <div id="how-it-works" className="how-it-works-container">
      <div className="how-it-works-left">
        <h2 className="scroll-title">{t('homepage.homeTestFeature.mainTitle')}</h2>
      </div>
      <div className="how-it-works-middle">
        <ArrowStack blueArrowHeights={blueArrowHeights} />
      </div>
      <div className="how-it-works-right">
        <AnimationDescription sectionRefs={sectionRefs} />
      </div>
    </div>
  );
};

export default ScrollAnimatedArrows;
