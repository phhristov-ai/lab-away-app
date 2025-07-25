import React from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    name: string;
    address: string;
    phone: string;
    city: string;
    country: string;
    email: string;
  };
  
  const ShippingDetails: React.FC<Props> = ({
    name, address, phone, city, country, email,
  }) => {
    const { t } = useTranslation();
    return (
      <>
        <tr className="order-summary-section">
          <td colSpan={2}><strong>{t('checkout.shipping.title')}</strong></td>
        </tr>
        <tr>
          <td className="shipping-left">
            {name}<br />
            {address}<br />
            {phone}<br />
            {city}<br />
            {country}
          </td>
          <td className="shipping-right">
            <div className="email-centered">{email}</div>
          </td>
        </tr>
      </>
    );
  };
  
  export default ShippingDetails;
  