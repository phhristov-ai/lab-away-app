import { CardCvcElement, CardExpiryElement, CardNumberElement } from '@stripe/react-stripe-js';
import './StripeForm.css';
import { useCreditCardForm } from '../../../hooks/useCreditCardForm';
import { SetStateAction, useState } from 'react';
import visaIcon from '../../../assets/icons/payment/visa.svg';
import mastercardIcon from '../../../assets/icons/payment/mastercard.svg';
import amexIcon from '../../../assets/icons/payment/amex.svg';
import cardIcon from '../../../assets/icons/payment/card.svg';

const cardBrandIcons: Record<string, string | undefined> = {
  visa: visaIcon,
  mastercard: mastercardIcon,
  amex: amexIcon,
  default: cardIcon
};

const CreditCardForm = () => {
  const { stripe, t, handleSubmit, cardStyle } = useCreditCardForm();
  const [cardBrand, setCardBrand] = useState('default');

  const handleCardNumberChange = (event: { brand: SetStateAction<string>; }) => {
    if (event.brand && event.brand !== cardBrand) {
      setCardBrand(event.brand);
    }
  };

  const getCardIcon = (brand: string): string | undefined => {
    return cardBrandIcons[brand] || cardBrandIcons.default;
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="stripe-form">
        <div className="form-group card-number-with-icon">
          <label className="input-label">
            {t('checkout.payment.creditCard.cardNumber')}
          </label>
          <div className="stripe-input-wrapper">
            <CardNumberElement
              className="stripe-input"
              options={cardStyle}
              onChange={handleCardNumberChange}
            />
            {getCardIcon(cardBrand) && (
              <img
                src={getCardIcon(cardBrand)}
                alt={`${cardBrand} icon`}
                className="card-brand-icon"
              />
            )}
          </div>
        </div>

        <div className="form-row">
          <div className="form-group half-width">
            <label className="input-label">
              {t('checkout.payment.creditCard.expiryDate')}
            </label>
            <div className="stripe-input-wrapper">
              <CardExpiryElement className="stripe-input" options={cardStyle} />
            </div>
          </div>
          <div className="form-group half-width">
            <label className="input-label">
              {t('checkout.payment.creditCard.cvc')}
            </label>
            <div className="stripe-input-wrapper">
              <CardCvcElement className="stripe-input" options={cardStyle} />
            </div>
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