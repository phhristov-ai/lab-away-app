import React from 'react';
import { useTranslation } from 'react-i18next';

interface AnimationDescriptionProps {
    sectionRefs: React.RefObject<HTMLDivElement | null>[];
}

const AnimationDescription: React.FC<AnimationDescriptionProps> = ({ sectionRefs }) => {
    const { t } = useTranslation();

    return (
        <div className="how-it-works-right">
            {[0, 1, 2, 3].map((index) => {
                const featureKey = `homepage.homeTestFeature.sections.${index}`;
                return (
                    <div className="feature-block" key={index} ref={sectionRefs[index]}>
                        <h2>{t(`${featureKey}.title`)}</h2>
                        <p>{t(`${featureKey}.text`)}</p>
                    </div>
                );
            })}
        </div>
    );
};

export default AnimationDescription;
