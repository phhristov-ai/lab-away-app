import React, { useEffect, useState } from 'react';
import './CookieConsentFooter.css';
import LabAwayButton from './LabAwayButton';
import { useConsent } from '../../context/consent/ConsentProvider';

interface Props {
    onOpenSettings: () => void;
}

const CookieConsentFooter: React.FC<Props> = ({ onOpenSettings }) => {

  const { consent, setInteracted } = useConsent();

  if (consent.hasInteracted) return null;

    return (
        <div className="cookie-footer">
            <div className="cookie-message">
                We are using cookies to give you the best experience on our website. <br />
                You can find out more about which cookies we use or change preferences in{" "}
                <span className="cookie-settings-link" onClick={onOpenSettings}>
                    settings
                </span>.
            </div>
            <LabAwayButton className="accept-button" onClick={setInteracted}>
                Accept
            </LabAwayButton>
        </div>
    );
};

export default CookieConsentFooter;
