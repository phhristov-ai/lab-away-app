import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { ConsentState, ConsentContextType } from './types';
import {
  loadGoogleAds,
  removeGoogleAds,
  getStoredConsent,
  saveConsent,
  ensureGoogleAnalytics,
} from './consentUtils';

const defaultConsent: ConsentState = {
  necessary: true,
  analytics: true,
  marketing: true,
};

const ConsentContext = createContext<ConsentContextType>({
  consent: defaultConsent,
  updateConsent: () => {},
});

export const ConsentProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [consent, setConsent] = useState<ConsentState>(() => getStoredConsent() || defaultConsent);

  useEffect(() => {
    saveConsent(consent);
    ensureGoogleAnalytics();

    if (consent.marketing) loadGoogleAds();
  }, []);

  useEffect(() => {
    console.log('[Consent Updated]', consent);
    saveConsent(consent);

    if (consent.marketing) loadGoogleAds();
    else removeGoogleAds();
  }, [consent.marketing]);

  const updateConsent = (newConsent: Partial<ConsentState>) => {
    setConsent((prev) => ({
      ...prev,
      ...newConsent,
      necessary: true,
    }));
  };

  const contextValue = useMemo(() => ({ consent, updateConsent }), [consent]);

  return <ConsentContext.Provider value={contextValue}>{children}</ConsentContext.Provider>;
};

export const useConsent = () => useContext(ConsentContext);
