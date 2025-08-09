import { useCallback } from 'react';
import { useTranslation } from 'react-i18next';

enum PaymentProvider {
  STRIPE = 'STRIPE',
  PAYPAL = 'PAYPAL',
}

type Address = {
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  country: string;
  postCode: string;
};

type OrderItem = {
  slug: string;
  quantity: number;
  price: number;
};

type CreateOrderPayload = {
  billingEmail: string;
  billingPhone: string;
  billingAddress: Address;
  shippingAddress: Address;
  items: OrderItem[];
  paymentProvider: PaymentProvider;
  language: Language;
};

type Language = 'EN' | 'DE' | 'BG';

export const useCreateOrder = () => {
  const { i18n } = useTranslation();

  const createOrder = useCallback(async (orderData: Omit<CreateOrderPayload, 'language'>) => {
    const payload: CreateOrderPayload = {
      ...orderData,
      language: i18n.language.toUpperCase() as Language,
    };

    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(`Failed to create order: ${error}`);
    }

    return await response.json();
  }, [i18n.language]);

  return { createOrder };
};