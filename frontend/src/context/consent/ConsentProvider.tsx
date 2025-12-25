import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { ConsentState } from './types';
import {
  getStoredConsent,
  saveConsent,
} from './consentUtils';

interface ConsentContextType {
  consent: ConsentState;
  setInteracted: () => void;
}

const defaultConsent: ConsentState = { hasInteracted: false };

const ConsentContext = createContext<ConsentContextType>({
  consent: defaultConsent,
  setInteracted: () => { },
});

export const ConsentProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [consent, setConsent] = useState<ConsentState>(getStoredConsent());

  const setInteracted = useCallback(() => {
    const updated = { hasInteracted: true };
    setConsent(updated);
    saveConsent(updated);
  }, []);

  const value = useMemo(
    () => ({ consent, setInteracted }),
    [consent, setInteracted]
  );

  return (
    <ConsentContext.Provider value={value}>
      {children}
    </ConsentContext.Provider>
  );
};


export const useConsent = () => useContext(ConsentContext);

