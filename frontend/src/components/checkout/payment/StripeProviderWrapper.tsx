// components/StripeProviderWrapper.tsx
import React from 'react';
import { Elements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

const stripePromise = loadStripe('pk_test_PgWSKAkynPEgvZGCxLWEiA81');

const StripeProviderWrapper = ({ children }: { children: React.ReactNode }) => (
  <Elements stripe={stripePromise}>
    {children}
  </Elements>
);

export default StripeProviderWrapper;
