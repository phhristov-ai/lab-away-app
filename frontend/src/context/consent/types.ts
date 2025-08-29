export interface ConsentState {
  necessary: boolean;
  analytics: boolean;
  marketing: boolean;
}

export interface ConsentContextType {
  consent: ConsentState;
  updateConsent: (newConsent: Partial<ConsentState>) => void;
}
