// components/StripeProviderWrapper.tsx
import React from 'react';
import { Elements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

const stripePromise = loadStripe(process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || '');

const StripeProviderWrapper = ({ children }: { children: React.ReactNode }) => (
  <Elements stripe={stripePromise}>
    {children}
  </Elements>
);

export default StripeProviderWrapper;
