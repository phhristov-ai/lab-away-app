import { CardCvcElement, CardExpiryElement, CardNumberElement } from '@stripe/react-stripe-js';
import './StripeForm.css';
import { useCreditCardForm } from '../../../hooks/useCreditCardForm';

const CreditCardForm = () => {
  const { stripe, t, handleSubmit, cardStyle } = useCreditCardForm();

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