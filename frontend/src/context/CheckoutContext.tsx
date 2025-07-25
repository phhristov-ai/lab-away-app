import React, { createContext, useContext, useState, ReactNode } from 'react';

interface CheckoutContextType {
  billingData: Record<string, string>;
  setBillingData: React.Dispatch<React.SetStateAction<Record<string, string>>>;
  shippingData: Record<string, string>;
  setShippingData: React.Dispatch<React.SetStateAction<Record<string, string>>>;
}

const CheckoutContext = createContext<CheckoutContextType | undefined>(undefined);

export const CheckoutProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [billingData, setBillingData] = useState<Record<string, string>>({});
  const [shippingData, setShippingData] = useState<Record<string, string>>({});

  return (
    <CheckoutContext.Provider value={{ billingData, setBillingData, shippingData, setShippingData }}>
      {children}
    </CheckoutContext.Provider>
  );
};

export const useCheckout = () => {
  const context = useContext(CheckoutContext);
  if (!context) {
    throw new Error('useCheckout must be used within a CheckoutProvider');
  }
  return context;
};
