import React from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    name: string;
    address: string;
    phone: string;
    city: string;
    countryName: string;
    email: string;
    countryCode: string;
  };
  
  const ShippingDetails: React.FC<Props> = ({
    name, address, phone, city, countryName, countryCode, email,
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
            {countryName}
          </td>
          <td></td>
          <td className="shipping-right">
            <div className="email-centered">{email}</div>
          </td>
        </tr>
      </>
    );
  };
  
  export default ShippingDetails;
  