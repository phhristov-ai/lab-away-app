import React, { useState } from 'react';
import './GDPRPopup.css';
import gdprLogo from '../../../assets/icons/gdpr-logo.our_.blue_.updated.webp';
import LabAwayButton from '../button/LabAwayButton';
import CookieToggle from './CookieToggle';
import { ConsentState } from '../../../context/consent/types';
import { useTranslation } from 'react-i18next';

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
    const { t } = useTranslation();
    
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
                <button className="gdpr-close-button" onClick={onClose} aria-label={t("gdpr.close")}>
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
                        {t("gdpr.privacyOverview")}
                    </button>
                    <button
                        type="button"
                        className={`gdpr-tab ${activeTab === 'necessary' ? 'active' : ''}`}
                        onClick={() => setActiveTab('necessary')}
                    >
                        {t("gdpr.necessaryCookies")}
                    </button>
                </div>

                <div className="gdpr-main">
                    <div className="gdpr-content">
                        {(activeTab === 'overview' || showNecessaryMobile) && (
                            <div>
                                <h2 className="gdpr-section-title">{t("gdpr.privacyOverview")}</h2>
                                <p>
                                    {t("gdpr.overviewText")}
                                </p>
                            </div>
                        )}
                        <button
                            type="button"
                            className={`gdpr-mobile-title ${showNecessaryMobile ? 'open' : ''}`}
                            onClick={() => setShowNecessaryMobile((prev) => !prev)}
                        >
                            {t("gdpr.necessaryCookies")}
                        </button>

                        {(activeTab === 'necessary' || showNecessaryMobile) && (
                            <div
                                className={`gdpr-section-collapsible ${showNecessaryMobile ? 'active' : ''
                                    }`}
                            >
                                <h2 className='gdpr-header-desktop'>{t("gdpr.necessaryCookies")}</h2>
                                <p>
                                    {t("gdpr.necessaryText")}
                                </p>
                                <CookieToggle
                                    label={t("gdpr.analyticsCookies")}
                                    value={analyticsEnabled}
                                    onChange={() => setAnalyticsEnabled(prev => !prev)}
                                />
                                <CookieToggle
                                    label={t("gdpr.marketingCookies")}
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
                                {t("gdpr.enableAll")}
                            </LabAwayButton>
                            <LabAwayButton className="save-button" onClick={handleInteraction}>
                                {t("gdpr.saveSettings")}
                            </LabAwayButton>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GDPRPopup;

