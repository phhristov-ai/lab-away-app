export interface ConsentState {

  hasInteracted: boolean;
}

export interface ConsentContextType {
  consent: ConsentState;
  updateConsent: (newConsent: Partial<ConsentState>) => void;
}
