import React, { useEffect, useRef, useState } from 'react';
import './ScrollAnimatedArrows.css';
import ArrowStack from './ArrowStack';
import { useTranslation } from 'react-i18next';
import AnimationDescription from './AnimationDescription';

const ScrollAnimatedArrows: React.FC = () => {
    const { t } = useTranslation();
    const sectionRefs = [
        React.useRef<HTMLDivElement>(null),
        React.useRef<HTMLDivElement>(null),
        React.useRef<HTMLDivElement>(null),
        React.useRef<HTMLDivElement>(null),
    ];

    const [blueArrowHeights, setBlueArrowHeights] = useState(['0px', '0px', '0px', '0px']);

    useEffect(() => {
        const offset = 150;

        const MAX_ARROW_HEIGHT = 180;

        const onScroll = () => {
            const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
            const updatedHeights = sectionRefs.map((ref) => {
                if (!ref.current) return '0px';
                const sectionTop = ref.current.offsetTop - offset;
                if (scrollTop > sectionTop) {
                    const sectionHeight = ref.current.clientHeight;
                    const scaledHeight = Math.min(sectionHeight, MAX_ARROW_HEIGHT);
                    return `${scaledHeight}px`;
                }
                return '0px';
            });
            setBlueArrowHeights(updatedHeights);
        };

        window.addEventListener('scroll', onScroll);
        onScroll();

        return () => window.removeEventListener('scroll', onScroll);
    }, []);

    return (
        <div id="how-it-works" className="three-column-container equal-width">
            {/* LEFT COLUMN */}
            <div className="column left-column">
                <h1 className="scroll-title">{t('homepage.homeTestFeature.mainTitle')}</h1>
            </div>
            {/* MIDDLE COLUMN */}
            <div className="column middle-column">
                <ArrowStack blueArrowHeights={blueArrowHeights} />
            </div>
            {/* RIGHT COLUMN */}
            <AnimationDescription sectionRefs={sectionRefs} />
        </div>
    );
};

export default ScrollAnimatedArrows;
