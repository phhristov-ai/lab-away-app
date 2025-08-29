import React, { useState } from 'react';
import './GDPRPopup.css';
import gdprLogo from '../../assets/icons/gdpr-logo.our_.blue_.updated.png';
import LabAwayButton from './LabAwayButton';
import { useConsent } from '../../context/consent/ConsentProvider';
import CookieToggle from './CookieToggle';

interface GDPRPopupProps {
    onClose: () => void;
}

const GDPRPopup: React.FC<GDPRPopupProps> = ({ onClose }) => {
    const [activeTab, setActiveTab] = useState<'overview' | 'necessary'>('overview');
    const [showNecessaryMobile, setShowNecessaryMobile] = useState(false);
    const { consent, updateConsent } = useConsent();

    const handleEnableAll = () => {
        updateConsent({ analytics: true, marketing: true });
        onClose();
    };

    const handleSaveSettings = () => {
        onClose();
    };

    return (
        <div className="gdpr-popup-overlay">
            <div className="gdpr-popup">
                <div className="gdpr-logo-mobile">
                    <img src={gdprLogo} alt="GDPR Logo" className="gdpr-logo" />
                </div>
                <button className="gdpr-close-button" onClick={onClose} aria-label="Close GDPR Popup">
                    &times;
                </button>

                {/* Desktop Sidebar */}
                <div className="gdpr-sidebar">
                    <div className="gdpr-logo-container">
                        <img src={gdprLogo} alt="GDPR Logo" className="gdpr-logo" />
                    </div>
                    <button
                        type="button"
                        className={`gdpr-tab ${activeTab === 'overview' ? 'active' : ''}`}
                        onClick={() => setActiveTab('overview')}
                    >
                        Privacy Overview
                    </button>
                    <button
                        type="button"
                        className={`gdpr-tab ${activeTab === 'necessary' ? 'active' : ''}`}
                        onClick={() => setActiveTab('necessary')}
                    >
                        Strictly Necessary Cookies
                    </button>
                </div>

                <div className="gdpr-main">
                    <div className="gdpr-content">
                        {(activeTab === 'overview' || showNecessaryMobile) && (
                        <div>
                            <h2 className="gdpr-section-title">Privacy Overview</h2>
                            <p>
                                Strictly Necessary Cookie should be enabled at all times so that we can save your preferences for cookie settings.
                            </p>
                        </div>
                        )}
                        {/* Mobile collapsible title */}
                        <div
                            className={`gdpr-mobile-title ${showNecessaryMobile ? 'open' : ''}`}
                            onClick={() => setShowNecessaryMobile((prev) => !prev)}
                        >
                            Strictly Necessary Cookies
                        </div>

                        {/* Desktop tab OR mobile collapsible */}
                        {(activeTab === 'necessary' || showNecessaryMobile) && (
                            <div
                                className={`gdpr-section-collapsible ${showNecessaryMobile ? 'active' : ''
                                    }`}
                            >
                                <h2 className='gdpr-header-desktop'>Strictly Necessary Cookies</h2>
                                <p>
                                    If you disable this cookie, we will not be able to save your preferences. This means that every time you visit this website you will need to enable or disable cookies again.
                                </p>
                                <CookieToggle
                                    label="Analytics Cookies"
                                    value={consent.analytics}
                                    onChange={(value) => updateConsent({ analytics: value })}
                                />
                                <CookieToggle
                                    label="Marketing Cookies"
                                    value={consent.marketing}
                                    onChange={(value) => updateConsent({ marketing: value })}
                                />
                            </div>
                        )}
                    </div>

                    <div className="gdpr-buttons-container">
                        <hr className="gdpr-separator" />
                        <div className="gdpr-buttons">
                            <LabAwayButton className="enable-button" onClick={handleEnableAll}>
                                Enable All
                            </LabAwayButton>
                            <LabAwayButton className="save-button" onClick={handleSaveSettings}>
                                Save Settings
                            </LabAwayButton>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GDPRPopup;
