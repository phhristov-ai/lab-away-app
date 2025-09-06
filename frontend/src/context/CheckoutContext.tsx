import React, { createContext, useContext, useState, ReactNode, useMemo } from 'react';

interface CheckoutContextType {
  billingData: Record<string, string>;
  setBillingData: React.Dispatch<React.SetStateAction<Record<string, string>>>;
  shippingData: Record<string, string>;
  setShippingData: React.Dispatch<React.SetStateAction<Record<string, string>>>;
}

const CheckoutContext = createContext<CheckoutContextType | undefined>(undefined);

const STORAGE_KEYS = {
  billing: 'checkout_billingData',
  shipping: 'checkout_shippingData',
};

export const CheckoutProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [billingData, setBillingData] = useState<Record<string, string>>(() => {
    const saved = localStorage.getItem(STORAGE_KEYS.billing);
    return saved ? JSON.parse(saved) : {};
  });

  const [shippingData, setShippingData] = useState<Record<string, string>>(() => {
    const saved = localStorage.getItem(STORAGE_KEYS.shipping);
    return saved ? JSON.parse(saved) : {};
  });

  React.useEffect(() => {
    localStorage.setItem(STORAGE_KEYS.billing, JSON.stringify(billingData));
  }, [billingData]);

  React.useEffect(() => {
    localStorage.setItem(STORAGE_KEYS.shipping, JSON.stringify(shippingData));
  }, [shippingData]);

  const contextValue = useMemo(() => ({
    billingData,
    setBillingData,
    shippingData,
    setShippingData,
  }), [billingData, shippingData]);

  return (
    <CheckoutContext.Provider value={contextValue}>
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
