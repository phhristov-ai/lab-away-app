import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { ConsentState, ConsentContextType } from './types';
import {
    loadGoogleAnalytics,
    removeGoogleAnalytics,
    loadGoogleAds,
    removeGoogleAds,
    getStoredConsent,
    saveConsent,
} from './consentUtils';

const defaultConsent: ConsentState = {
    necessary: true,
    analytics: false,
    marketing: false,
};

const ConsentContext = createContext<ConsentContextType>({
    consent: defaultConsent,
    updateConsent: () => { },
});

export const ConsentProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [consent, setConsent] = useState<ConsentState>(() => getStoredConsent() || defaultConsent);

    useEffect(() => {
        saveConsent(consent);

        if (consent.analytics) loadGoogleAnalytics();
        else removeGoogleAnalytics();

        if (consent.marketing) loadGoogleAds();
        else removeGoogleAds();
    }, [consent]);

    const updateConsent = (newConsent: Partial<ConsentState>) => {
        setConsent((prev) => ({
            ...prev,
            ...newConsent,
            necessary: true,
        }));
    };

    const contextValue = useMemo(
        () => ({ consent, updateConsent }),
        [consent, updateConsent]
    );

    return (
        <ConsentContext.Provider value={contextValue}>
            {children}
        </ConsentContext.Provider>
    );
};

export const useConsent = () => useContext(ConsentContext);
