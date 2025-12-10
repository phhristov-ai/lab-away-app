import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
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
  setInteracted: () => {},
});

export const ConsentProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [consent, setConsent] = useState<ConsentState>(getStoredConsent());

  const setInteracted = () => {
    const updated = { hasInteracted: true };
    setConsent(updated);
    saveConsent(updated);
  };

  return (
    <ConsentContext.Provider value={{ consent, setInteracted }}>
      {children}
    </ConsentContext.Provider>
  );
};

export const useConsent = () => useContext(ConsentContext);

