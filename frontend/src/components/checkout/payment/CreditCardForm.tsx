import React from 'react';
import { CardCvcElement, CardExpiryElement, CardNumberElement, useStripe } from '@stripe/react-stripe-js';
import './StripeForm.css';
import { useCreateOrder } from '../../../hooks/useCreateOrder';
import { useCheckout } from '../../../context/CheckoutContext';
import { useCart } from '../../../context/CartContext';
import { useStripePayment } from '../../../hooks/useStripePayment';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import i18n from '../../../i18n/i18n';

enum PaymentProvider {
  STRIPE = 'STRIPE',
  PAYPAL = 'PAYPAL',
}

const CreditCardForm = () => {
  const stripe = useStripe();
  const { createOrder } = useCreateOrder();
  const { billingData } = useCheckout();
  const { shippingData } = useCheckout();
  const { confirmPayment } = useStripePayment();
  const navigate = useNavigate();
  const { state, dispatch } = useCart();
  const { t } = useTranslation();
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const orderData = {
        billingEmail: billingData.email,
        billingPhone: billingData.phone,
        paymentProvider: PaymentProvider.STRIPE,
        billingAddress: {
          firstName: billingData.firstName,
          lastName: billingData.lastName,
          address: billingData.address,
          city: billingData.city,
          country: billingData.country,
          postCode: billingData.postcode,
        },
        shippingAddress: {
          firstName: shippingData.firstName,
          lastName: shippingData.lastName,
          address: shippingData.address,
          city: shippingData.city,
          country: shippingData.country,
          postCode: shippingData.postcode,
        },
        items: state.items.map(item => ({
          slug: item.slug,
          quantity: item.quantity,
          price: item.price,
        })),
        language: i18n.language
      };

      // 2. Create order
      const order = await createOrder(orderData);
      console.log('✅ Order created:', order);


      console.log('🔐 clientSecret:', order.clientSecret);

      // 4. Confirm payment with Stripe
      const result = await confirmPayment(order.clientSecret, billingData);

      if (result.error) {
        console.error('[❌ Payment Error]', result.error.message);
        return;
      }

      if (result.paymentIntent?.status === 'succeeded') {
        console.log('✅ Payment successful');

        // 5. Optionally confirm the order in backend
        await fetch(`${process.env.REACT_APP_API_BASE_URL}/orders/confirm/${order.orderNumber}`, {
          method: 'POST',
        });


        dispatch({ type: 'CLEAR_CART' });
        console.log(shippingData);
        navigate('/success', {
          state: {
            orderNumber: order.orderNumber,
            email: billingData.email,
            total: order.total,
            paymentMethod: 'Cash on delivery',
            shippingAddress: shippingData,
          },
        });

      }

    } catch (error) {
      console.error('❌ Error during checkout:', error);
    }
  };

  const cardStyle = {
    style: {
      base: {
        color: '#000',
        fontSize: '16px',
        backgroundColor: '#fff',
        fontWeight: 400,
        fontFamily: '"Roboto", sans-serif',
        '::placeholder': {
          color: '#888',
        },
      },
      invalid: {
        color: '#e87c03',
      },
    },
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="stripe-form">
        <div className="form-group">
          <label className="input-label">{t('checkout.payment.creditCard.cardNumber')}</label>
          <CardNumberElement className="stripe-input" options={cardStyle} />
        </div>

        <div className="form-row">
          <div className="form-group half-width">
            <label className="input-label">{t('checkout.payment.creditCard.expiryDate')}</label>
            <CardExpiryElement className="stripe-input" options={cardStyle} />
          </div>
          <div className="form-group half-width">
            <label className="input-label">{t('checkout.payment.creditCard.cvc')}</label>
            <CardCvcElement className="stripe-input" options={cardStyle} />
          </div>
        </div>
      </div>
      <button type="submit" className="place-order-button" disabled={!stripe}>
        {t('checkout.payment.creditCard.placeOrder')}
      </button>
    </form>
  );
};

export default CreditCardForm;
