import React, { useState } from 'react';
import './CookieConsentFooter.css';
import LabAwayButton from './LabAwayButton';

interface Props {
    onOpenSettings: () => void;
}

const CookieConsentFooter: React.FC<Props> = ({ onOpenSettings }) => {
    const [visible, setVisible] = useState<boolean>(() => {
        // Check if user already accepted cookies
        return localStorage.getItem('cookieConsent') !== 'accepted';
    });

    const handleAccept = () => {
        localStorage.setItem('cookieConsent', 'accepted');
        setVisible(false);
    };

    if (!visible) return null;

    return (
        <div className="cookie-footer">
            <div className="cookie-message">
                We are using cookies to give you the best experience on our website. <br />
                You can find out more about which cookies we are using or switch them off in{' '}
                <span className="cookie-settings-link" onClick={onOpenSettings}>
                    settings
                </span>.
            </div>
            <LabAwayButton className="accept-button" onClick={handleAccept}>
                Accept
            </LabAwayButton>
        </div>
    );
};

export default CookieConsentFooter;
