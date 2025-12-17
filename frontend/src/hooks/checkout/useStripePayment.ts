import { CardNumberElement, useElements, useStripe } from "@stripe/react-stripe-js";

export const useStripePayment = () => {
  const stripe = useStripe();
  const elements = useElements();

  const confirmPayment = async (clientSecret: string, billingData: any) => {
    if (!stripe || !elements) throw new Error('Stripe not loaded');

    const cardNumberElement = elements.getElement(CardNumberElement);
    if (!cardNumberElement) throw new Error('CardNumberElement not found');

    return await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: cardNumberElement,
        billing_details: {
          name: `${billingData.fullName}`,
          email: billingData.email,
          phone: billingData.phone,
          address: {
            line1: billingData.address,
            city: billingData.city,
            country: billingData.country,
            postal_code: billingData.postcode,
          },
        },
      },
    });
  };

  return { confirmPayment };
};
