import { useState, useEffect } from 'react';
import './ScrollToTopButton.css';
import { useTranslation } from 'react-i18next';

const ScrollToTopButton = () => {
    const [isVisible, setIsVisible] = useState(false);
    const { t } = useTranslation();

    useEffect(() => {
        const handleScroll = () => {
            setIsVisible(window.scrollY > 300);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const handleClick = () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        isVisible && (
            <button
                className="scroll-to-top-button"
                onClick={handleClick}
                aria-label={t("misc.scrollToTop")}
            >
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    viewBox="0 0 24 24"
                >
                    <polyline points="18 15 12 9 6 15" />
                </svg>
            </button>
        )
    );
};

export default ScrollToTopButton;
