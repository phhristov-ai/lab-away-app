import { useCallback } from 'react';

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
};

export const useCreateOrder = () => {
  const createOrder = useCallback(async (orderData: CreateOrderPayload) => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(`Failed to create order: ${error}`);
    }

    return await response.json();
  }, []);

  return { createOrder };
};
