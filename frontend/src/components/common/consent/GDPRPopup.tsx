import React, { useState } from 'react';
import './GDPRPopup.css';
import gdprLogo from '../../../assets/icons/gdpr-logo.our_.blue_.updated.webp';
import LabAwayButton from '../button/LabAwayButton';
import CookieToggle from './CookieToggle';
import { ConsentState } from '../../../context/consent/types';

interface GDPRPopupProps {
    onClose: () => void;
}

interface GDPRPopupProps {
  onClose: () => void;
  onConsentUpdate: (consent: ConsentState) => void;
}

const GDPRPopup: React.FC<GDPRPopupProps> = ({ onClose, onConsentUpdate }) => {
    const [activeTab, setActiveTab] = useState<'overview' | 'necessary'>('overview');
    const [showNecessaryMobile, setShowNecessaryMobile] = useState(false);

    const [analyticsEnabled, setAnalyticsEnabled] = useState(false);
    const [marketingEnabled, setMarketingEnabled] = useState(false);

    const handleInteraction = () => {
        const updatedConsent: ConsentState = { hasInteracted: true };
        onConsentUpdate(updatedConsent);
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
                        <div
                            className={`gdpr-mobile-title ${showNecessaryMobile ? 'open' : ''}`}
                            onClick={() => setShowNecessaryMobile((prev) => !prev)}
                        >
                            Strictly Necessary Cookies
                        </div>

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
                                    value={analyticsEnabled}
                                    onChange={() => setAnalyticsEnabled(prev => !prev)}
                                />
                                <CookieToggle
                                    label="Marketing Cookies"
                                    value={marketingEnabled}
                                    onChange={() => setMarketingEnabled(prev => !prev)}
                                />
                            </div>
                        )}
                    </div>

                    <div className="gdpr-buttons-container">
                        <hr className="gdpr-separator" />
                        <div className="gdpr-buttons">
                            <LabAwayButton className="enable-button" onClick={handleInteraction}>
                                Enable All
                            </LabAwayButton>
                            <LabAwayButton className="save-button" onClick={handleInteraction}>
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

