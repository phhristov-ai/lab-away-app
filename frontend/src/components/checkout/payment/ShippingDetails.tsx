import React from 'react';
import { useTranslation } from 'react-i18next';
import './ShippingDetails.css';

type Props = {
  name: string;
  address: string;
  phone: string;
  city: string;
  countryName: string;
  email: string;
};

const ShippingDetails: React.FC<Props> = ({
  name, address, phone, city, countryName, email,
}) => {
  const { t } = useTranslation();
  return (
    <>
      <tr className="order-summary-section">
        <td colSpan={3}><strong>{t('checkout.shipping.title')}</strong></td>
      </tr>
      <tr>
        <td className="shipping-left" colSpan={3}>
          {name}<br />
          {address}<br />
          {phone ? `+${phone}` : null}<br />
          {city}<br />
          {countryName}
        </td>
        <td colSpan={2} className="shipping-right">
          <div className="email-centered">{email}</div>
        </td>
      </tr>
    </>
  );
};

export default ShippingDetails;
